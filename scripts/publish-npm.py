#!/usr/bin/env python3
"""
Zernikalos NPM Publish Script
Publishes NPM packages to GitHub Packages

Python version of the original publish-npm.sh script
"""

import sys
import argparse
from pathlib import Path
from typing import Optional, List, Tuple, Any
from common import add_common_arguments
from base_builder import BaseBuilder


class NpmPublisher(BaseBuilder):
    """Main class for NPM package publishing functionality"""

    def __init__(self):
        super().__init__("Zernikalos NPM Publisher")
    
    def authentication(self) -> bool:
        """Setup and verify authentication"""
        if not self.github_token:
            self.print_warning("GitHub token not found")
            self.print_status("Setting up npm authentication...")
            if not self.get_github_credentials():
                return False
        
        # Set npm authentication token
        self.npm.set_auth_token(self.github_token)
        self.print_success("npm authentication configured")
        return True
    
    def build(self) -> bool:
        """Build packages with webpack"""
        self.print_status("Building packages with webpack...")
        return self.gradle.js_browser_production_webpack()
    
    def build_verify(self) -> bool:
        """Verify that build directory exists and artifacts are ready"""
        build_dir = self.project_root / "build" / "js"
        if not build_dir.exists():
            self.print_warning("Build directory 'build/js' does not exist")
            self.print_status("Building packages automatically...")
            return self.build()
        return True
    
    def publish(self, package_name: Optional[str] = None, *args, **kwargs) -> bool:
        """
        Publish artifacts to the repository
        
        Args:
            package_name: Optional package name to publish (None = all packages)
            *args: Variable positional arguments
            **kwargs: Variable keyword arguments
            
        Returns:
            True if publish is successful, False otherwise
        """
        return self._publish_workspace(package_name)
    
    def get_publish_info(self) -> bool:
        """Get and display publication information"""
        packages = self._list_packages()
        if not packages:
            return False
        
        print()
        self.print_status("To publish a specific package: python publish-npm.py <package_name>")
        self.print_status("To publish all packages: python publish-npm.py -a")
        return True
    
    def _check_tool(self) -> bool:
        """Check if npm is available"""
        return self.check_npm()
    
    def _get_default_action(self) -> int:
        """Get the default action when no specific action is provided"""
        self._list_packages()
        return self.get_publish_info() and 0 or 1
    
    def _handle_action(self, args: Any) -> int:
        """Handle specific action based on command line arguments"""
        if args.list:
            self._list_packages()
            return self.get_publish_info() and 0 or 1
        elif args.all:
            return 0 if self.publish() else 1
        elif args.package:
            return 0 if self.publish(args.package) else 1
        else:
            return self._get_default_action()
    
    # Private methods for internal operations
    def _list_packages(self, exclude_test: bool = True) -> List[Tuple[str, str]]:
        """List available packages, optionally excluding test packages"""
        self.print_status("Available packages in build/js/packages/@zernikalos/:")

        packages = self.npm.list_packages(exclude_test=exclude_test)
        
        if not packages:
            self.print_error("No packages found in build/js/packages/@zernikalos/")
        else:
            for package_name, version in packages:
                print(f"  - {package_name} (v{version})")

        return packages
    
    def _publish_workspace(self, package_filter: Optional[str] = None) -> bool:
        """Publish packages using npm workspace with optional filter"""
        workspace_dir = self.project_root / "build" / "js"

        if not workspace_dir.exists():
            self.print_error("Workspace directory 'build/js' not found")
            return False

        self.print_status("Publishing workspace packages...")

        # Get workspace packages info
        packages = self._list_packages()
        if not packages:
            return False

        # Filter packages if specified
        if package_filter:
            # Additional safety: exclude test packages even when filter is specified
            filtered_packages = [(name, version) for name, version in packages 
                               if package_filter in name and 'test' not in name.lower()]
            if not filtered_packages:
                self.print_error(f"No packages found matching filter: {package_filter}")
                return False
            packages = filtered_packages
        else:
            # When no filter specified, only publish the main zernikalos package
            # Test packages are already excluded by list_packages()
            main_package = [(name, version) for name, version in packages if name == "zernikalos"]
            if not main_package:
                self.print_error("Main zernikalos package not found")
                return False
            packages = main_package

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

        # Ensure npm has the auth token
        self.npm.set_auth_token(self.github_token)

        # Publish using npm tool
        success, stdout, stderr = self.npm.publish_workspace(
            workspace_dir,
            package_filter=package_filter,
            show_output=True
        )

        if success:
            self.print_success(f"Workspace packages published successfully!")
            for package_name, version in packages:
                self.print_status(f"  - @zernikalos/{package_name} v{version}")
                self.print_status(f"    Available at: https://npm.pkg.github.com/@zernikalos/{package_name}")
            return True
        else:
            self.print_error("npm publish failed")
            return False



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
