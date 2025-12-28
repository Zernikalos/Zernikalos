"""
Git Tool
Provides Git operations for Zernikalos scripts
"""

import subprocess
from pathlib import Path
from typing import Tuple, Optional, List


class GitTool:
    """Tool for Git operations"""
    
    def __init__(self, project_root: Path = None):
        """
        Initialize Git tool
        
        Args:
            project_root: Root directory of the project (defaults to current directory)
        """
        self.project_root = project_root or Path.cwd()
    
    def check_status(self) -> Tuple[bool, str]:
        """
        Check if git working directory is clean
        
        Returns:
            Tuple of (is_clean: bool, output: str)
        """
        try:
            result = subprocess.run(
                ['git', 'status', '--porcelain'],
                capture_output=True,
                text=True,
                check=True,
                cwd=self.project_root
            )
            is_clean = not result.stdout.strip()
            return is_clean, result.stdout
        except subprocess.CalledProcessError as e:
            return False, f"Failed to check git status: {e}"
        except FileNotFoundError:
            return False, "git command not found"
    
    def check_tag_exists(self, tag: str) -> bool:
        """
        Check if a git tag exists
        
        Args:
            tag: Tag name to check (e.g., 'v1.0.0')
            
        Returns:
            True if tag exists, False otherwise
        """
        try:
            result = subprocess.run(
                ['git', 'tag', '-l', tag],
                capture_output=True,
                text=True,
                check=True,
                cwd=self.project_root
            )
            return tag in result.stdout
        except subprocess.CalledProcessError:
            return False
        except FileNotFoundError:
            return False
    
    def list_tags(self) -> List[str]:
        """
        List all git tags
        
        Returns:
            List of tag names
        """
        try:
            result = subprocess.run(
                ['git', 'tag', '-l'],
                capture_output=True,
                text=True,
                check=True,
                cwd=self.project_root
            )
            return [tag.strip() for tag in result.stdout.split('\n') if tag.strip()]
        except subprocess.CalledProcessError:
            return []
        except FileNotFoundError:
            return []
    
    def push_branch(self, branch: str = "main", remote: str = "origin") -> bool:
        """
        Push a branch to remote repository
        
        Args:
            branch: Branch name to push
            remote: Remote name (default: origin)
            
        Returns:
            True if successful, False otherwise
        """
        try:
            subprocess.run(
                ['git', 'push', remote, branch],
                check=True,
                cwd=self.project_root
            )
            return True
        except subprocess.CalledProcessError as e:
            print(f"Failed to push branch {branch}: {e}")
            return False
        except FileNotFoundError:
            print("git command not found")
            return False
    
    def push_tag(self, tag: str, remote: str = "origin") -> bool:
        """
        Push a tag to remote repository
        
        Args:
            tag: Tag name to push
            remote: Remote name (default: origin)
            
        Returns:
            True if successful, False otherwise
        """
        try:
            subprocess.run(
                ['git', 'push', remote, tag],
                check=True,
                cwd=self.project_root
            )
            return True
        except subprocess.CalledProcessError as e:
            print(f"Failed to push tag {tag}: {e}")
            return False
        except FileNotFoundError:
            print("git command not found")
            return False
    
    def push_branch_and_tag(self, branch: str = "main", tag: Optional[str] = None, remote: str = "origin") -> bool:
        """
        Push branch and optionally a tag to remote repository
        
        Args:
            branch: Branch name to push (default: main)
            tag: Optional tag name to push
            remote: Remote name (default: origin)
            
        Returns:
            True if successful, False otherwise
        """
        success = True
        
        # Push branch
        if not self.push_branch(branch, remote):
            success = False
        
        # Push tag if provided
        if tag and not self.push_tag(tag, remote):
            success = False
        
        return success
    
    def get_current_branch(self) -> Optional[str]:
        """
        Get the current git branch name
        
        Returns:
            Branch name or None if error
        """
        try:
            result = subprocess.run(
                ['git', 'rev-parse', '--abbrev-ref', 'HEAD'],
                capture_output=True,
                text=True,
                check=True,
                cwd=self.project_root
            )
            return result.stdout.strip()
        except subprocess.CalledProcessError:
            return None
        except FileNotFoundError:
            return None
    
    def is_repository(self) -> bool:
        """
        Check if current directory is a git repository
        
        Returns:
            True if git repository, False otherwise
        """
        try:
            subprocess.run(
                ['git', 'rev-parse', '--git-dir'],
                capture_output=True,
                check=True,
                cwd=self.project_root
            )
            return True
        except (subprocess.CalledProcessError, FileNotFoundError):
            return False
    
    def get_last_release_tag(self) -> Optional[str]:
        """
        Get the last release tag (format v*.*.*)
        
        Uses git describe to find the most recent tag matching the pattern v*.*.*
        which corresponds to semantic version tags.
        
        Returns:
            Last release tag name (e.g., 'v0.6.0') or None if no tags found
        """
        try:
            result = subprocess.run(
                ['git', 'describe', '--tags', '--abbrev=0', '--match', 'v*.*.*'],
                capture_output=True,
                text=True,
                check=True,
                cwd=self.project_root
            )
            tag = result.stdout.strip()
            return tag if tag else None
        except subprocess.CalledProcessError:
            # No tags found or no matching tags
            return None
        except FileNotFoundError:
            return None
    
    def get_commits_since_tag(self, tag: str) -> List[str]:
        """
        Get list of commit messages since a specific tag
        
        Retrieves commit subject lines (first line of commit message) from the
        specified tag to HEAD. Used for analyzing Conventional Commits.
        
        Args:
            tag: Tag name to get commits from (e.g., 'v0.6.0')
            
        Returns:
            List of commit messages (subject lines only)
        """
        try:
            result = subprocess.run(
                ['git', 'log', f'{tag}..HEAD', '--pretty=format:%s'],
                capture_output=True,
                text=True,
                check=True,
                cwd=self.project_root
            )
            commits = [line.strip() for line in result.stdout.split('\n') if line.strip()]
            return commits
        except subprocess.CalledProcessError:
            return []
        except FileNotFoundError:
            return []
    
    def get_current_commit_hash(self, short: bool = True) -> Optional[str]:
        """
        Get the current commit hash
        
        Args:
            short: If True, return short hash (7 chars), otherwise full hash
            
        Returns:
            Commit hash or None if error
        """
        try:
            format_str = '%h' if short else '%H'
            result = subprocess.run(
                ['git', 'rev-parse', format_str, 'HEAD'],
                capture_output=True,
                text=True,
                check=True,
                cwd=self.project_root
            )
            return result.stdout.strip()
        except subprocess.CalledProcessError:
            return None
        except FileNotFoundError:
            return None

