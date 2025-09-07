#!/usr/bin/env python3
"""
Zernikalos Common Utilities
Shared functionality for all Zernikalos scripts
"""

import os
import sys
import subprocess
import getpass
import argparse
from pathlib import Path
from typing import Optional, Tuple


class Colors:
    """ANSI color codes for terminal output"""
    RED = '\033[0;31m'
    GREEN = '\033[0;32m'
    YELLOW = '\033[1;33m'
    BLUE = '\033[0;34m'
    NC = '\033[0m'  # No Color


class BaseScript:
    """Base class for all Zernikalos scripts with common functionality"""
    
    def __init__(self, script_name: str = "Zernikalos Script"):
        self.script_name = script_name
        self.github_user = os.environ.get('GITHUB_ACTOR') or os.environ.get('GITHUB_USER', 'Zernikalos')
        self.github_token = os.environ.get('GITHUB_TOKEN')
        self.project_root = Path.cwd()
        
    def print_status(self, message: str) -> None:
        """Print status message with blue color"""
        print(f"{Colors.BLUE}[INFO]{Colors.NC} {message}")
        
    def print_success(self, message: str) -> None:
        """Print success message with green color"""
        print(f"{Colors.GREEN}[SUCCESS]{Colors.NC} {message}")
        
    def print_warning(self, message: str) -> None:
        """Print warning message with yellow color"""
        print(f"{Colors.YELLOW}[WARNING]{Colors.NC} {message}")
        
    def print_error(self, message: str) -> None:
        """Print error message with red color"""
        print(f"{Colors.RED}[ERROR]{Colors.NC} {message}")
        
    def print_header(self, message: str) -> None:
        """Print header message with emphasis"""
        print(f"\n{Colors.BLUE}{'='*60}{Colors.NC}")
        print(f"{Colors.BLUE}{message:^60}{Colors.NC}")
        print(f"{Colors.BLUE}{'='*60}{Colors.NC}")
        
    def check_directory(self) -> bool:
        """Check if we're in the right directory"""
        if not (self.project_root / "build.gradle.kts").exists():
            self.print_error("This script must be run from the Zernikalos project root directory")
            return False
        return True
        
    def get_github_credentials(self) -> bool:
        """Get GitHub credentials from various sources"""
        # Check if credentials are already set
        if self.github_user and self.github_token:
            self.print_success("Using credentials from environment variables")
            return True
            
        # Set default user if not provided
        if not self.github_user:
            self.github_user = "Zernikalos"
            self.print_status(f"Using default GitHub organization: {self.github_user}")
            
        # Prompt for token if not provided
        if not self.github_token:
            self.print_status("GitHub credentials required")
            self.print_status(f"Organization: {self.github_user} (default)")
            
            try:
                self.github_token = getpass.getpass("Enter GitHub access token: ")
                if not self.github_token:
                    self.print_error("GitHub access token is required")
                    return False
            except KeyboardInterrupt:
                print()
                self.print_error("Operation cancelled by user")
                return False
                
        return True
        
    def check_npm(self) -> bool:
        """Check if npm is available"""
        try:
            result = subprocess.run(['npm', '--version'], 
                                  capture_output=True, text=True, check=True)
            self.print_success(f"npm version: {result.stdout.strip()}")
            return True
        except (subprocess.CalledProcessError, FileNotFoundError):
            self.print_error("npm is not installed or not in PATH")
            self.print_error("Please install Node.js and npm first")
            return False
            
    def check_gradle(self) -> bool:
        """Check if gradle is available"""
        try:
            result = subprocess.run(['./gradlew', '--version'], 
                                  capture_output=True, text=True, check=True)
            self.print_success("Gradle available")
            return True
        except (subprocess.CalledProcessError, FileNotFoundError):
            self.print_error("Gradle wrapper not found or not executable")
            self.print_error("Please ensure gradlew is present and executable")
            return False
            
    def get_project_version(self) -> Optional[str]:
        """Get the current project version from VERSION.txt"""
        version_file = self.project_root / "VERSION.txt"
        if not version_file.exists():
            self.print_error("VERSION.txt not found")
            return None
            
        try:
            version = version_file.read_text().strip()
            self.print_status(f"Project version: {version}")
            return version
        except IOError as e:
            self.print_error(f"Failed to read VERSION.txt: {e}")
            return None
            
    def run_gradle_command(self, command: str, *args) -> bool:
        """Run a gradle command with error handling"""
        try:
            cmd = ['./gradlew', command] + list(args)
            result = subprocess.run(cmd, check=True, capture_output=True, text=True)
            return True
        except subprocess.CalledProcessError as e:
            self.print_error(f"Failed to run gradle command '{command}': {e}")
            if e.stderr:
                print(f"Error details: {e.stderr}")
            return False
            
    def confirm_action(self, message: str, default: bool = False) -> bool:
        """Ask user for confirmation"""
        try:
            prompt = f"{message} ({'Y/n' if default else 'y/N'}): "
            response = input(prompt).strip().lower()
            
            if not response:
                return default
            return response in ['y', 'yes']
        except KeyboardInterrupt:
            print()
            self.print_warning("Operation cancelled by user")
            return False
            
    def set_credentials_from_args(self, args) -> None:
        """Set credentials from command line arguments"""
        if hasattr(args, 'user') and args.user:
            self.github_user = args.user
        if hasattr(args, 'token') and args.token:
            self.github_token = args.token


def add_common_arguments(parser: argparse.ArgumentParser) -> None:
    """Add common command line arguments to a parser"""
    parser.add_argument('-u', '--user', 
                       help='GitHub organization/user')
    parser.add_argument('-t', '--token',
                       help='GitHub access token')


def validate_version(version: str) -> bool:
    """Validate version format (X.Y.Z)"""
    import re
    if not re.match(r'^[0-9]+\.[0-9]+\.[0-9]+$', version):
        return False
    return True


def check_git_status() -> Tuple[bool, str]:
    """Check if git working directory is clean"""
    try:
        result = subprocess.run(['git', 'status', '--porcelain'], 
                              capture_output=True, text=True, check=True)
        is_clean = not result.stdout.strip()
        return is_clean, result.stdout
    except subprocess.CalledProcessError as e:
        return False, f"Failed to check git status: {e}"


def check_version_exists(version: str) -> bool:
    """Check if version tag already exists"""
    try:
        result = subprocess.run(['git', 'tag', '-l'], 
                              capture_output=True, text=True, check=True)
        return f"v{version}" in result.stdout
    except subprocess.CalledProcessError:
        return False


def push_to_remote(branch: str = "main", tag: str = None) -> bool:
    """Push changes and optionally a tag to remote repository"""
    try:
        # Push branch
        subprocess.run(['git', 'push', 'origin', branch], check=True)
        
        # Push tag if provided
        if tag:
            subprocess.run(['git', 'push', 'origin', tag], check=True)
            
        return True
    except subprocess.CalledProcessError as e:
        print(f"Failed to push to remote: {e}")
        return False
