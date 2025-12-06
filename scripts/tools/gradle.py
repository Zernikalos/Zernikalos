"""
Gradle Tool
Provides Gradle operations for Zernikalos scripts
"""

import subprocess
from pathlib import Path
from typing import Optional, Tuple, List


class GradleTool:
    """Tool for Gradle operations"""
    
    def __init__(self, project_root: Path = None, gradlew_path: str = "./gradlew"):
        """
        Initialize Gradle tool
        
        Args:
            project_root: Root directory of the project (defaults to current directory)
            gradlew_path: Path to gradle wrapper (default: ./gradlew)
        """
        self.project_root = project_root or Path.cwd()
        self.gradlew_path = gradlew_path
    
    def check_available(self) -> Tuple[bool, Optional[str]]:
        """
        Check if Gradle wrapper is available
        
        Returns:
            Tuple of (is_available: bool, version_info: Optional[str])
        """
        try:
            result = subprocess.run(
                [self.gradlew_path, '--version'],
                capture_output=True,
                text=True,
                check=True,
                cwd=self.project_root
            )
            return True, result.stdout
        except subprocess.CalledProcessError as e:
            return False, f"Gradle wrapper error: {e.stderr if e.stderr else str(e)}"
        except FileNotFoundError:
            return False, "Gradle wrapper not found or not executable"
    
    def run_command(self, command: str, *args, capture_output: bool = True, show_output: bool = False) -> Tuple[bool, Optional[str], Optional[str]]:
        """
        Run a Gradle command
        
        Args:
            command: Gradle task or command to run
            *args: Additional arguments to pass to gradle
            capture_output: Whether to capture output (default: True)
            show_output: Whether to print output to console (default: False)
            
        Returns:
            Tuple of (success: bool, stdout: Optional[str], stderr: Optional[str])
        """
        try:
            cmd = [self.gradlew_path, command] + list(args)
            
            if capture_output:
                result = subprocess.run(
                    cmd,
                    check=True,
                    capture_output=True,
                    text=True,
                    cwd=self.project_root
                )
                if show_output:
                    if result.stdout:
                        print(result.stdout)
                    if result.stderr:
                        print(result.stderr)
                return True, result.stdout, result.stderr
            else:
                result = subprocess.run(
                    cmd,
                    check=True,
                    cwd=self.project_root
                )
                return True, None, None
        except subprocess.CalledProcessError as e:
            error_msg = e.stderr if e.stderr else str(e)
            if show_output:
                print(f"Error: {error_msg}")
            return False, None, error_msg
        except FileNotFoundError:
            error_msg = "Gradle wrapper not found or not executable"
            if show_output:
                print(f"Error: {error_msg}")
            return False, None, error_msg
    
    def build(self, *args) -> bool:
        """
        Build the project
        
        Args:
            *args: Additional arguments to pass to gradle
            
        Returns:
            True if successful, False otherwise
        """
        success, _, _ = self.run_command('build', *args)
        return success
    
    def set_version(self, version: str) -> bool:
        """
        Set project version
        
        Args:
            version: Version string (e.g., '0.4.1')
            
        Returns:
            True if successful, False otherwise
        """
        success, _, _ = self.run_command('setVersion', f'-PnewVersion={version}')
        return success
    
    def upgrade_kotlin_package_lock(self) -> bool:
        """
        Upgrade Kotlin package lock
        
        Returns:
            True if successful, False otherwise
        """
        success, _, _ = self.run_command('kotlinUpgradePackageLock')
        return success
    
    def update_version(self) -> bool:
        """
        Generate version-dependent files
        
        Returns:
            True if successful, False otherwise
        """
        success, _, _ = self.run_command('updateVersion')
        return success
    
    def release_commit(self) -> bool:
        """
        Create release commit and tag
        
        Returns:
            True if successful, False otherwise
        """
        success, _, _ = self.run_command('releaseCommit')
        return success
    
    def js_browser_production_webpack(self) -> bool:
        """
        Build JavaScript packages with webpack
        
        Returns:
            True if successful, False otherwise
        """
        success, _, _ = self.run_command('jsBrowserProductionWebpack')
        return success
    
    def publish_android_debug(self, user: str, access_token: str) -> bool:
        """
        Publish Android Debug artifacts to Maven Repository
        
        Args:
            user: GitHub user/organization
            access_token: GitHub access token
            
        Returns:
            True if successful, False otherwise
        """
        success, _, _ = self.run_command(
            'publishAndroidDebugPublicationToMavenRepository',
            f'-Puser={user}',
            f'-Paccess_token={access_token}'
        )
        return success
    
    def publish_android_release(self, user: str, access_token: str) -> bool:
        """
        Publish Android Release artifacts to Maven Repository
        
        Args:
            user: GitHub user/organization
            access_token: GitHub access token
            
        Returns:
            True if successful, False otherwise
        """
        success, _, _ = self.run_command(
            'publishAndroidReleasePublicationToMavenRepository',
            f'-Puser={user}',
            f'-Paccess_token={access_token}'
        )
        return success
    
    def publish_all_publications(self, user: str, access_token: str) -> bool:
        """
        Publish ALL publications to Maven Repository
        
        Args:
            user: GitHub user/organization
            access_token: GitHub access token
            
        Returns:
            True if successful, False otherwise
        """
        success, _, _ = self.run_command(
            'publishAllPublicationsToMavenRepository',
            f'-Puser={user}',
            f'-Paccess_token={access_token}'
        )
        return success

