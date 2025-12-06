"""
NPM Tool
Provides NPM operations for Zernikalos scripts
"""

import os
import subprocess
import json
from pathlib import Path
from typing import Optional, List, Tuple


class NpmTool:
    """Tool for NPM operations"""
    
    def __init__(self, project_root: Path = None):
        """
        Initialize NPM tool
        
        Args:
            project_root: Root directory of the project (defaults to current directory)
        """
        self.project_root = project_root or Path.cwd()
        self.github_token: Optional[str] = None
    
    def check_available(self) -> Tuple[bool, Optional[str]]:
        """
        Check if npm is available
        
        Returns:
            Tuple of (is_available: bool, version: Optional[str])
        """
        try:
            result = subprocess.run(
                ['npm', '--version'],
                capture_output=True,
                text=True,
                check=True
            )
            version = result.stdout.strip()
            return True, version
        except subprocess.CalledProcessError:
            return False, None
        except FileNotFoundError:
            return False, None
    
    def set_auth_token(self, token: str) -> None:
        """
        Set GitHub authentication token for npm
        
        Args:
            token: GitHub access token
        """
        self.github_token = token
        os.environ['NODE_AUTH_TOKEN'] = token
    
    def get_auth_env(self) -> dict:
        """
        Get environment variables for npm authentication
        
        Returns:
            Dictionary with environment variables
        """
        env = os.environ.copy()
        if self.github_token:
            env['NODE_AUTH_TOKEN'] = self.github_token
        return env
    
    def list_packages(self, packages_dir: Path = None, exclude_test: bool = True) -> List[Tuple[str, str]]:
        """
        List available packages in the packages directory
        
        Args:
            packages_dir: Directory containing packages (default: build/js/packages/@zernikalos)
            exclude_test: Whether to exclude test packages (default: True)
            
        Returns:
            List of tuples (package_name, version)
        """
        if packages_dir is None:
            packages_dir = self.project_root / "build" / "js" / "packages" / "@zernikalos"
        
        packages = []
        
        if not packages_dir.exists():
            return packages
        
        for package_dir in packages_dir.iterdir():
            if package_dir.is_dir():
                package_name = package_dir.name
                
                # Exclude test packages
                if exclude_test and ('test' in package_name.lower() or package_name.endswith('-test')):
                    continue
                
                package_json = package_dir / "package.json"
                
                if package_json.exists():
                    try:
                        with open(package_json, 'r') as f:
                            data = json.load(f)
                            version = data.get('version', 'unknown')
                            packages.append((package_name, version))
                    except (json.JSONDecodeError, IOError):
                        pass
        
        return packages
    
    def publish_workspace(self, workspace_dir: Path, package_filter: Optional[str] = None, 
                         show_output: bool = True) -> Tuple[bool, Optional[str], Optional[str]]:
        """
        Publish packages using npm workspace
        
        Args:
            workspace_dir: Directory containing the workspace
            package_filter: Optional package name filter (e.g., 'zernikalos')
            show_output: Whether to print output to console (default: True)
            
        Returns:
            Tuple of (success: bool, stdout: Optional[str], stderr: Optional[str])
        """
        if not workspace_dir.exists():
            return False, None, f"Workspace directory not found: {workspace_dir}"
        
        try:
            env = self.get_auth_env()
            cmd = ['npm', 'publish']
            
            # Add workspace filter
            if package_filter:
                cmd.extend(['--workspace', f'@zernikalos/{package_filter}'])
            else:
                # Default to main zernikalos package
                cmd.extend(['--workspace', '@zernikalos/zernikalos'])
            
            result = subprocess.run(
                cmd,
                capture_output=True,
                text=True,
                env=env,
                cwd=workspace_dir
            )
            
            if show_output:
                if result.stdout:
                    print("npm output:")
                    print(result.stdout)
                if result.stderr:
                    print("npm errors:")
                    print(result.stderr)
            
            if result.returncode == 0:
                return True, result.stdout, result.stderr
            else:
                return False, result.stdout, result.stderr
                
        except Exception as e:
            return False, None, str(e)
    
    def run_command(self, command: str, *args, cwd: Optional[Path] = None, 
                   capture_output: bool = True, show_output: bool = False) -> Tuple[bool, Optional[str], Optional[str]]:
        """
        Run an npm command
        
        Args:
            command: npm command to run (e.g., 'publish', 'install')
            *args: Additional arguments
            cwd: Working directory (default: project_root)
            capture_output: Whether to capture output (default: True)
            show_output: Whether to print output to console (default: False)
            
        Returns:
            Tuple of (success: bool, stdout: Optional[str], stderr: Optional[str])
        """
        if cwd is None:
            cwd = self.project_root
        
        try:
            cmd = ['npm', command] + list(args)
            env = self.get_auth_env()
            
            if capture_output:
                result = subprocess.run(
                    cmd,
                    capture_output=True,
                    text=True,
                    env=env,
                    cwd=cwd
                )
                if show_output:
                    if result.stdout:
                        print(result.stdout)
                    if result.stderr:
                        print(result.stderr)
                return result.returncode == 0, result.stdout, result.stderr
            else:
                result = subprocess.run(
                    cmd,
                    env=env,
                    cwd=cwd
                )
                return result.returncode == 0, None, None
                
        except subprocess.CalledProcessError as e:
            error_msg = e.stderr if e.stderr else str(e)
            if show_output:
                print(f"Error: {error_msg}")
            return False, None, error_msg
        except FileNotFoundError:
            error_msg = "npm command not found"
            if show_output:
                print(f"Error: {error_msg}")
            return False, None, error_msg

