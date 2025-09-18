#!/usr/bin/env python3
"""
Zernikalos NPM Publish Script
Publishes NPM packages to GitHub Packages

Python version of the original publish-npm.sh script
"""

import os
import sys
import subprocess
import json
import argparse
from pathlib import Path
from typing import Optional, List, Tuple
from common import BaseScript, add_common_arguments


class NpmPublisher(BaseScript):
    """Main class for NPM package publishing functionality"""
    
    def __init__(self):
        super().__init__("Zernikalos NPM Publisher")
        
    def setup_npm_auth(self) -> bool:
        """Setup npm authentication using environment variables"""
        self.print_status("Setting up npm authentication...")
        
        if not self.get_github_credentials():
            return False
            
        # Set npm authentication environment variables
        os.environ['NPM_AUTH_TOKEN'] = self.github_token
        
        self.print_success("npm authentication configured via environment variables")
        return True
            
    def build_packages(self) -> bool:
        """Build packages with webpack"""
        self.print_status("Building packages with webpack...")
        return self.run_gradle_command('jsBrowserProductionWebpack')
            
    def check_build_directory(self) -> bool:
        """Check if build directory exists"""
        build_dir = self.project_root / "build" / "js"
        if not build_dir.exists():
            self.print_warning("Build directory 'build/js' does not exist")
            self.print_status("Building packages automatically...")
            return self.build_packages()
        return True
        
    def check_npm_auth(self) -> bool:
        """Check if npm authentication is properly configured"""
        if not self.github_token:
            self.print_warning("GitHub token not found")
            self.print_status("Setting up npm authentication...")
            return self.setup_npm_auth()
            
        # Ensure environment variables are set
        os.environ['NPM_AUTH_TOKEN'] = self.github_token
        
        self.print_success("npm authentication configured")
        return True
        
    def list_packages(self) -> List[Tuple[str, str]]:
        """List available packages"""
        self.print_status("Available packages in build/js/packages/@zernikalos/:")
        
        packages_dir = self.project_root / "build" / "js" / "packages" / "@zernikalos"
        packages = []
        
        if not packages_dir.exists():
            self.print_error("No packages found in build/js/packages/@zernikalos/")
            return packages
            
        for package_dir in packages_dir.iterdir():
            if package_dir.is_dir():
                package_name = package_dir.name
                package_json = package_dir / "package.json"
                
                if package_json.exists():
                    try:
                        with open(package_json, 'r') as f:
                            data = json.load(f)
                            version = data.get('version', 'unknown')
                            packages.append((package_name, version))
                            print(f"  - {package_name} (v{version})")
                    except (json.JSONDecodeError, IOError):
                        print(f"  - {package_name} (error reading version)")
                        
        return packages
        
    def publish_workspace(self, package_filter: str = None) -> bool:
        """Publish packages using npm workspace with optional filter"""
        workspace_dir = self.project_root / "build" / "js"
        
        if not workspace_dir.exists():
            self.print_error("Workspace directory 'build/js' not found")
            return False
            
        self.print_status("Publishing workspace packages...")
        
        # Get workspace packages info
        packages = self.list_packages()
        if not packages:
            return False
            
        # Filter packages if specified
        if package_filter:
            filtered_packages = [(name, version) for name, version in packages if package_filter in name]
            if not filtered_packages:
                self.print_error(f"No packages found matching filter: {package_filter}")
                return False
            packages = filtered_packages
            
        # Show packages to be published
        self.print_status("Packages to be published:")
        for package_name, version in packages:
            print(f"  - @zernikalos/{package_name} (v{version})")
            
        # Confirm publication
        try:
            response = input(f"Proceed with publishing {len(packages)} package(s)? (y/N): ")
            if response.lower() != 'y':
                self.print_warning("Publication cancelled")
                return True
        except KeyboardInterrupt:
            print()
            self.print_warning("Operation cancelled by user")
            return True
            
        # Publish using workspace
        self.print_status("Publishing to GitHub Packages using workspace...")
        
        try:
            # Set up environment for npm publish
            env = os.environ.copy()
            env['NPM_AUTH_TOKEN'] = self.github_token
            
            # Build npm publish command
            cmd = ['npm', 'publish']
            
            # Add workspace filter if specified
            if package_filter:
                cmd.extend(['--workspace', f'@zernikalos/{package_filter}'])
            else:
                # Publish all workspaces except test packages
                cmd.extend(['--workspaces', '--exclude', '@zernikalos/zernikalos-test'])
            
            # Run npm publish
            result = subprocess.run(cmd, capture_output=True, text=True, env=env, cwd=workspace_dir)
            
            # Show npm output
            if result.stdout:
                print("npm output:")
                print(result.stdout)
            if result.stderr:
                print("npm errors:")
                print(result.stderr)
            
            if result.returncode == 0:
                self.print_success(f"Workspace packages published successfully!")
                for package_name, version in packages:
                    self.print_status(f"  - @zernikalos/{package_name} v{version}")
                    self.print_status(f"    Available at: https://npm.pkg.github.com/@zernikalos/{package_name}")
                return True
            else:
                self.print_error(f"npm publish failed with exit code {result.returncode}")
                return False
            
        except Exception as e:
            self.print_error(f"Failed to publish workspace: {e}")
            return False
            
    def publish_package(self, package_name: str) -> bool:
        """Publish a specific package using workspace"""
        return self.publish_workspace(package_name)
            
    def publish_all_packages(self) -> bool:
        """Publish all packages using workspace (excluding test packages)"""
        return self.publish_workspace()
        
    def run(self, args):
        """Main execution method"""
        # Set credentials from command line if provided
        self.set_credentials_from_args(args)
        
        # Check prerequisites
        if not self.check_directory():
            return 1
            
        if not self.check_npm():
            return 1
            
        # Setup authentication and build if needed
        if not self.check_npm_auth():
            return 1
            
        if not self.check_build_directory():
            return 1
            
        # Execute action
        if args.list:
            self.list_packages()
            print()
            self.print_status("To publish a specific package: python publish-npm.py <package_name>")
            self.print_status("To publish all packages: python publish-npm.py -a")
            return 0
            
        elif args.all:
            return 0 if self.publish_all_packages() else 1
            
        elif args.package:
            return 0 if self.publish_package(args.package) else 1
            
        else:
            # Default action: list packages
            self.list_packages()
            print()
            self.print_status("To publish a specific package: python publish-npm.py <package_name>")
            self.print_status("To publish all packages: python publish-npm.py -a")
            return 0


def main():
    """Main entry point"""
    parser = argparse.ArgumentParser(
        description="Zernikalos NPM Publish Script - Python version",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  python publish-npm.py                                    # Show available packages
  python publish-npm.py -l                                 # List available packages
  python publish-npm.py -a                                 # Publish all packages
  python publish-npm.py zernikalos                         # Publish specific package
  python publish-npm.py -u Zernikalos -t TOKEN zernikalos # With custom credentials

Prerequisites:
  1. Ensure you have npm installed and configured
  2. GitHub credentials (one of these methods):
     - Environment variables: GITHUB_USER and GITHUB_TOKEN
     - Command line: -u USER -t TOKEN
     - Interactive prompt (only token required, user defaults to Zernikalos)
  3. The script will automatically:
     - Configure npm authentication via environment variables (more secure)
     - Build packages with webpack
     - Publish packages to GitHub Packages
        """
    )
    
    parser.add_argument('-l', '--list', action='store_true',
                       help='List available packages')
    parser.add_argument('-a', '--all', action='store_true',
                       help='Publish all packages')
    parser.add_argument('package', nargs='?',
                       help='Package name to publish')
    
    # Add common arguments
    add_common_arguments(parser)
    
    args = parser.parse_args()
    
    # Create publisher and run
    publisher = NpmPublisher()
    return publisher.run(args)


def run_npm_publish(github_user: str, github_token: str, action: str = "list", package_name: str = None) -> bool:
    """Run NPM publish functionality programmatically"""
    # Create a mock args object
    class MockArgs:
        def __init__(self, list_flag=False, all_flag=False, user="", token="", package=None):
            self.list = list_flag
            self.all = all_flag
            self.user = user
            self.token = token
            self.package = package
    
    args = MockArgs(
        list_flag=(action == "list"),
        all_flag=(action == "all"),
        user=github_user,
        token=github_token,
        package=package_name
    )
    
    publisher = NpmPublisher()
    if args.user:
        publisher.github_user = args.user
    if args.token:
        publisher.github_token = args.token
    
    return publisher.run(args) == 0


if __name__ == '__main__':
    sys.exit(main())
