"""
PublisherManager
Business logic for publishing all Zernikalos artifacts
"""

from pathlib import Path
from typing import Optional
from .base_builder import GitHubCredentials
from .npm_publisher import NpmPublisher
from .maven_publisher import MavenPublisher
from .types import PublishResult, StatusInfo, PublishInfo
from tools import GradleTool, NpmTool


class PublisherManager:
    """Main class for publishing all Zernikalos artifacts - pure business logic"""
    
    def __init__(self, project_root: Path = None):
        """
        Initialize publisher manager
        
        Args:
            project_root: Root directory of the project
        """
        self.project_root = project_root or Path.cwd()
        self.gradle = GradleTool(self.project_root)
        self.npm = NpmTool(self.project_root)
        
    def publish_npm(self, credentials: GitHubCredentials) -> PublishResult:
        """
        Publish NPM packages
        
        Args:
            credentials: GitHub credentials for authentication
            
        Returns:
            PublishResult with success status and details
        """
        # Verify npm is available
        npm_available, npm_version = self.npm.check_available()
        if not npm_available:
            return PublishResult(
                success=False,
                target='npm',
                error_message="npm is not installed or not in PATH"
            )
        
        try:
            publisher = NpmPublisher(
                project_root=self.project_root,
                enabled_publications=["all"],
                credentials=credentials
            )
            
            exit_code = publisher.run()
            success = exit_code == 0
            
            return PublishResult(
                success=success,
                target='npm',
                error_message=None if success else "NPM publish failed",
                details={"exit_code": exit_code, "npm_version": npm_version}
            )
                
        except Exception as e:
            return PublishResult(
                success=False,
                target='npm',
                error_message=f"Failed to publish NPM packages: {e}"
            )
            
    def publish_maven(self, credentials: GitHubCredentials) -> PublishResult:
        """
        Publish Maven artifacts
        
        Args:
            credentials: GitHub credentials for authentication
            
        Returns:
            PublishResult with success status and details
        """
        # Verify gradle is available
        gradle_available, _ = self.gradle.check_available()
        if not gradle_available:
            return PublishResult(
                success=False,
                target='maven',
                error_message="Gradle wrapper not found or not executable"
            )
        
        try:
            publisher = MavenPublisher(
                project_root=self.project_root,
                enabled_publications=["all_publications"],
                credentials=credentials
            )
            
            exit_code = publisher.run()
            success = exit_code == 0
            
            return PublishResult(
                success=success,
                target='maven',
                error_message=None if success else "Maven publish failed",
                details={"exit_code": exit_code}
            )
                
        except Exception as e:
            return PublishResult(
                success=False,
                target='maven',
                error_message=f"Failed to publish Maven artifacts: {e}"
            )
    
    def publish_all(self, credentials: GitHubCredentials) -> PublishResult:
        """
        Publish all artifacts (NPM + Maven)
        
        Args:
            credentials: GitHub credentials for authentication
            
        Returns:
            PublishResult with success status and details for both targets
        """
        npm_result = self.publish_npm(credentials)
        maven_result = self.publish_maven(credentials)
        
        overall_success = npm_result.success and maven_result.success
        
        return PublishResult(
            success=overall_success,
            target='all',
            error_message=None if overall_success else "Some artifacts failed to publish",
            details={
                "npm": {
                    "success": npm_result.success,
                    "error": npm_result.error_message
                },
                "maven": {
                    "success": maven_result.success,
                    "error": maven_result.error_message
                }
            }
        )
        
    def get_status(self) -> StatusInfo:
        """
        Get current project status
        
        Returns:
            StatusInfo with project status details
        """
        # Check project version
        version_file = self.project_root / "VERSION.txt"
        version = None
        version_file_exists = version_file.exists()
        if version_file_exists:
            try:
                version = version_file.read_text().strip()
            except IOError:
                pass
        
        # Check build directories
        js_build = self.project_root / "build" / "js"
        android_build = self.project_root / "build"
        
        # Check tools status
        gradle_available, gradle_version = self.gradle.check_available()
        gradlew_path = None
        if gradle_available:
            gradlew_path = str(self.gradle.gradlew_path)
        
        npm_available, npm_version = self.npm.check_available()
        
        return StatusInfo(
            version=version,
            version_file_exists=version_file_exists,
            js_build_exists=js_build.exists(),
            android_build_exists=android_build.exists(),
            gradle_available=gradle_available,
            gradle_version=gradle_version,
            gradlew_path=gradlew_path,
            npm_available=npm_available,
            npm_version=npm_version,
            github_user=None,  # Not available in manager, set by CLI
            github_token_set=False  # Not available in manager, set by CLI
        )
    
    def get_info(self, credentials: Optional[GitHubCredentials] = None) -> PublishInfo:
        """
        Get detailed information about available packages and artifacts
        
        Args:
            credentials: Optional GitHub credentials for authentication
            
        Returns:
            PublishInfo with package and artifact details
        """
        npm_packages = []
        maven_artifacts = []
        error_message = None
        
        # Get NPM package info
        try:
            if credentials:
                npm_publisher = NpmPublisher(
                    project_root=self.project_root,
                    enabled_publications=None,  # None = show info
                    credentials=credentials
                )
                # The run() method with enabled_publications=None will show info
                # We need to extract this info, but for now we'll just note it was attempted
                npm_publisher.run()
                # TODO: Extract actual package info from publisher if needed
        except Exception as e:
            error_message = f"Could not get NPM info: {e}"
            
        # Get Maven artifact info
        try:
            if credentials:
                maven_publisher = MavenPublisher(
                    project_root=self.project_root,
                    enabled_publications=None,  # None = show info
                    credentials=credentials
                )
                maven_publisher.run()
                # TODO: Extract actual artifact info from publisher if needed
        except Exception as e:
            if error_message:
                error_message += f"; Could not get Maven info: {e}"
            else:
                error_message = f"Could not get Maven info: {e}"
        
        return PublishInfo(
            npm_packages=npm_packages if npm_packages else None,
            maven_artifacts=maven_artifacts if maven_artifacts else None,
            error_message=error_message if error_message else None
        )

