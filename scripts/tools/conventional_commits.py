"""
Conventional Commits Tool
Provides analysis of Conventional Commits and version calculation.

This tool analyzes commit messages following the Conventional Commits specification
to determine the appropriate version bump (major, minor, or patch) and calculates
the next version for both Maven (SNAPSHOT) and npm (next.COMMIT_HASH) formats.
"""

import re
from pathlib import Path
from typing import List, Optional, Tuple
from enum import Enum

from .git import GitTool


class VersionBump(Enum):
    """Version bump types"""
    MAJOR = "major"
    MINOR = "minor"
    PATCH = "patch"


class ConventionalCommitsTool:
    """Tool for analyzing Conventional Commits and calculating version bumps"""
    
    def __init__(self, project_root: Path = None):
        """
        Initialize Conventional Commits tool
        
        Args:
            project_root: Root directory of the project (defaults to current directory)
        """
        self.project_root = project_root or Path.cwd()
        self.git = GitTool(self.project_root)
    
    def analyze_commit_type(self, commit_message: str) -> Optional[VersionBump]:
        """
        Analyze commit message to determine version bump type
        
        Args:
            commit_message: Commit message to analyze
            
        Returns:
            VersionBump type (MAJOR, MINOR, PATCH) or None if not a conventional commit
        """
        if not commit_message:
            return None
        
        # Check for breaking changes (highest priority)
        if self._is_breaking_change(commit_message):
            return VersionBump.MAJOR
        
        # Check for feat (minor)
        if self._is_feat(commit_message):
            return VersionBump.MINOR
        
        # Check for fix (patch)
        if self._is_fix(commit_message):
            return VersionBump.PATCH
        
        return None
    
    def _is_breaking_change(self, commit_message: str) -> bool:
        """Check if commit is a breaking change"""
        # Pattern: feat! or feat(scope)! or BREAKING CHANGE in body
        breaking_patterns = [
            r'^feat!',
            r'^feat\([^)]+\)!',
            r'BREAKING CHANGE',
        ]
        
        for pattern in breaking_patterns:
            if re.search(pattern, commit_message, re.IGNORECASE | re.MULTILINE):
                return True
        return False
    
    def _is_feat(self, commit_message: str) -> bool:
        """Check if commit is a feature"""
        # Pattern: feat: or feat(scope):
        feat_pattern = r'^feat(\([^)]+\))?:'
        return bool(re.match(feat_pattern, commit_message, re.IGNORECASE))
    
    def _is_fix(self, commit_message: str) -> bool:
        """Check if commit is a fix"""
        # Pattern: fix: or fix(scope):
        fix_pattern = r'^fix(\([^)]+\))?:'
        return bool(re.match(fix_pattern, commit_message, re.IGNORECASE))
    
    def calculate_version_bump(self, base_version: str, commits: List[str]) -> VersionBump:
        """
        Calculate version bump type based on commits
        
        Args:
            base_version: Base version (e.g., '0.6.0')
            commits: List of commit messages
            
        Returns:
            VersionBump type (MAJOR, MINOR, PATCH)
        """
        if not commits:
            # No commits, default to patch
            return VersionBump.PATCH
        
        highest_bump = VersionBump.PATCH
        
        for commit in commits:
            bump_type = self.analyze_commit_type(commit)
            if bump_type:
                # Priority: MAJOR > MINOR > PATCH
                if bump_type == VersionBump.MAJOR:
                    return VersionBump.MAJOR
                elif bump_type == VersionBump.MINOR and highest_bump == VersionBump.PATCH:
                    highest_bump = VersionBump.MINOR
        
        return highest_bump
    
    def increment_version(self, version: str, bump_type: VersionBump) -> str:
        """
        Increment version based on bump type
        
        Args:
            version: Current version (e.g., '0.6.0')
            bump_type: Type of version bump
            
        Returns:
            Incremented version (e.g., '0.7.0')
        """
        parts = version.split('.')
        if len(parts) != 3:
            raise ValueError(f"Invalid version format: {version}. Expected X.Y.Z")
        
        major, minor, patch = map(int, parts)
        
        if bump_type == VersionBump.MAJOR:
            major += 1
            minor = 0
            patch = 0
        elif bump_type == VersionBump.MINOR:
            minor += 1
            patch = 0
        elif bump_type == VersionBump.PATCH:
            patch += 1
        
        return f"{major}.{minor}.{patch}"
    
    def calculate_next_version(self, base_version: str) -> Tuple[str, str, str, List[str], VersionBump]:
        """
        Calculate next version based on Conventional Commits
        
        Args:
            base_version: Base version from VERSION.txt (e.g., '0.6.0')
            
        Returns:
            Tuple of:
            - next_version: Next version (e.g., '0.7.0')
            - maven_version: Maven version with SNAPSHOT (e.g., '0.7.0-SNAPSHOT')
            - npm_version: npm version with next and commit hash (e.g., '0.7.0-next.42a2e33')
            - commits: List of commit messages analyzed
            - bump_type: Type of version bump applied
        """
        # Get last release tag
        last_tag = self.git.get_last_release_tag()
        if not last_tag:
            # No tags found, use v0.0.0 as base
            last_tag = "v0.0.0"
        
        # Get commits since last tag
        commits = self.git.get_commits_since_tag(last_tag)
        
        # Calculate version bump
        bump_type = self.calculate_version_bump(base_version, commits)
        
        # Increment version
        next_version = self.increment_version(base_version, bump_type)
        
        # Get current commit hash for npm version
        commit_hash = self.git.get_current_commit_hash(short=True) or "unknown"
        
        # Format versions
        maven_version = f"{next_version}-SNAPSHOT"
        npm_version = f"{next_version}-next.{commit_hash}"
        
        return next_version, maven_version, npm_version, commits, bump_type

