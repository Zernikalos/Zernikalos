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

