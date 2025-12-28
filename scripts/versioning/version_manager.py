"""
Version Manager
Pure business logic for version management - no I/O
"""

from pathlib import Path
from typing import Optional
from common import BaseScript, validate_version
from tools import ConventionalCommitsTool
from .types import ValidationResult, ReleaseResult, PushResult, VersionInfo


class VersionManager(BaseScript):
    """Version management functionality - pure business logic"""
    
    def __init__(self, project_root: Path = None):
        """
        Initialize version manager
        
        Args:
            project_root: Root directory of the project
        """
        super().__init__("Zernikalos Version Manager")
        if project_root:
            self.project_root = project_root
        self.conventional_commits = ConventionalCommitsTool(self.project_root)
        
    def get_current_version(self) -> Optional[str]:
        """
        Get current project version from VERSION.txt
        
        Returns:
            Current version string or None if error
        """
        return self.get_project_version()
    
    def execute_release(self, version: str) -> ReleaseResult:
        """
        Execute the release steps
        
        Args:
            version: Version string to release
            
        Returns:
            ReleaseResult with success status and details
        """
        steps_completed = []
        
        # Step 1: Set version
        if not self.gradle.set_version(version):
            return ReleaseResult(
                success=False,
                version=version,
                steps_completed=steps_completed,
                error_message="Failed to set version"
            )
        steps_completed.append("set_version")
        
        # Step 2: Upgrade Kotlin package lock
        if not self.gradle.upgrade_kotlin_package_lock():
            return ReleaseResult(
                success=False,
                version=version,
                steps_completed=steps_completed,
                error_message="Failed to upgrade Kotlin package lock"
            )
        steps_completed.append("upgrade_kotlin_package_lock")
        
        # Step 3: Generate version files
        # Pass version as project property to ensure cocoapods plugin reads it correctly
        if not self.gradle.update_version(version):
            return ReleaseResult(
                success=False,
                version=version,
                steps_completed=steps_completed,
                error_message="Failed to generate version files"
            )
        steps_completed.append("update_version")
        
        # Step 4: Create release commit and tag
        # Pass version as project property to ensure updateVersion (which releaseCommit depends on) reads it correctly
        if not self.gradle.release_commit(version):
            return ReleaseResult(
                success=False,
                version=version,
                steps_completed=steps_completed,
                error_message="Failed to create release commit and tag"
            )
        steps_completed.append("release_commit")
        
        return ReleaseResult(
            success=True,
            version=version,
            steps_completed=steps_completed
        )

    def push_release(self, version: str) -> PushResult:
        """
        Push changes to remote repository
        
        Args:
            version: Version string to push
            
        Returns:
            PushResult with success status
        """
        if self.git.push_branch_and_tag("main", f"v{version}"):
            return PushResult(success=True, version=version)
        else:
            return PushResult(
                success=False,
                version=version,
                error_message="Failed to push changes to remote"
            )
    
    def calculate_next_version(self) -> Optional[VersionInfo]:
        """
        Calculate next version based on Conventional Commits
        
        Returns:
            VersionInfo with calculated version details or None if error
        """
        base_version = self.get_project_version()
        if not base_version:
            return None
        
        last_tag = self.git.get_last_release_tag()
        
        try:
            next_version, maven_version, npm_version, commits, bump_type = \
                self.conventional_commits.calculate_next_version(base_version)
            
            return VersionInfo(
                base_version=base_version,
                next_version=next_version,
                maven_version=maven_version,
                npm_version=npm_version,
                commits=commits,
                bump_type=bump_type,
                last_tag=last_tag
            )
        except Exception:
            return None
    
    def validate_release(self, version: str) -> ValidationResult:
        """
        Validate version and check prerequisites for release
        
        Args:
            version: Version string to validate
            
        Returns:
            ValidationResult with validation status and error message if invalid
        """
        # Validate version format
        if not validate_version(version):
            return ValidationResult(
                is_valid=False,
                error_message="Invalid version format. Use X.Y.Z (e.g., 0.4.1)"
            )
        
        # Check we're in the right directory
        if not self.check_directory():
            return ValidationResult(
                is_valid=False,
                error_message="Not in Zernikalos project root directory"
            )
        
        # Check git status
        is_clean, git_output = self.git.check_status()
        if not is_clean:
            return ValidationResult(
                is_valid=False,
                error_message=f"Git working directory is not clean:\n{git_output}"
            )
        
        # Check if version already exists
        if self.git.check_tag_exists(f"v{version}"):
            return ValidationResult(
                is_valid=False,
                error_message=f"Version v{version} already exists as a tag"
            )
        
        return ValidationResult(is_valid=True)
