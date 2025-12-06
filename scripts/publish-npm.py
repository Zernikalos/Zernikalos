#!/usr/bin/env python3
"""
Zernikalos NPM Publish Script
Auxiliary script for publishing NPM packages to GitHub Packages

This script is designed to be called from publish-all.py, but can also be run independently.
"""

import sys
import argparse
from pathlib import Path
from typing import Optional, List, Tuple, Any, Dict
from common import add_common_arguments
from base_builder import BaseBuilder


class NpmPublisher(BaseBuilder):
    """Main class for NPM package publishing functionality"""

    def __init__(self, enabled_publications: Optional[List[str]] = None):
        super().__init__("Zernikalos NPM Publisher", enabled_publications=enabled_publications)
    
    def get_available_publications(self) -> List[Dict[str, str]]:
        """Get list of available publications"""
        return [
            {"id": "all", "name": "All Packages", "description": "Publish main zernikalos package"}
        ]
    
    def authentication(self) -> bool:
        """Setup and verify authentication"""
        if not self.github_token:
            self.print_warning("GitHub token not found")
            self.print_status("Setting up npm authentication...")
            if not self.get_github_credentials():
                return False
        
        # Set npm authentication token
        self.npm.set_auth_token(self.github_token)
        return True
    
    def build(self) -> bool:
        """Build packages with webpack"""
        return self.gradle.js_browser_production_webpack()
    
    def build_verify(self) -> bool:
        """Verify that build directory exists and artifacts are ready"""
        build_dir = self.project_root / "build" / "js"
        if not build_dir.exists():
            self._print_build_auto("build/js")
            return self.build()
        return True
    
    def _publish_publication(self, pub_id: str) -> bool:
        """
        Publish a specific publication by ID
        
        Args:
            pub_id: Publication ID to publish
            
        Returns:
            True if publish is successful, False otherwise
        """
        if pub_id == "all":
            return self._publish_workspace(None)
        else:
            self._print_unknown_publication(pub_id)
            return False
    
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
    
    def _handle_action(self, args: Any) -> int:
        """Handle specific action based on command line arguments"""
        # Override to handle list flag specially (needs to list packages first)
        if args.list:
            self._list_packages()
            return 0 if self.get_publish_info() else 1
        
        # Otherwise use base implementation
        return super()._handle_action(args)
    
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

        # Ensure npm has the auth token
        self.npm.set_auth_token(self.github_token)

        # Publish using npm tool
        success, stdout, stderr = self.npm.publish_workspace(
            workspace_dir,
            package_filter=package_filter,
            show_output=True
        )

        if success:
            for package_name, version in packages:
                self.print_status(f"  - @zernikalos/{package_name} v{version}")
                self.print_status(f"    Available at: https://npm.pkg.github.com/@zernikalos/{package_name}")
        
        return success


def _select_publications_interactive(publisher: NpmPublisher) -> Optional[List[str]]:
    """Interactive selection of publications to enable"""
    publications = publisher.get_available_publications()
    
    publisher._list_packages()
    print()
    publisher.print_status("Available publications:")
    for i, pub in enumerate(publications, 1):
        print(f"  {i}. {pub['name']} ({pub['id']}) - {pub['description']}")
    
    print()
    try:
        response = input("Select publication to publish (number, or 'all' for all): ").strip().lower()
        
        if response == 'all':
            return [pub['id'] for pub in publications]
        
        # Parse number
        if response.isdigit():
            index = int(response) - 1
            if 0 <= index < len(publications):
                return [publications[index]['id']]
        
        publisher.print_warning("Invalid selection. Nothing will be published.")
        return []
        
    except (ValueError, KeyboardInterrupt):
        print()
        publisher.print_warning("Operation cancelled by user")
        return None


def main():
    """
    Main entry point for standalone execution
    
    Note: This script is primarily designed to be called from publish-all.py.
    This function provides backward compatibility for direct execution.
    """
    parser = argparse.ArgumentParser(
        description="Zernikalos NPM Publish Script (auxiliary)",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  python publish-npm.py                                    # Show available packages
  python publish-npm.py -l                                 # List available packages
  python publish-npm.py -a                                 # Publish all packages

Note: For complete publishing workflow, use publish-all.py instead.
        """
    )

    parser.add_argument('-l', '--list', action='store_true',
                       help='List available packages')
    parser.add_argument('-a', '--all', action='store_true',
                       help='Publish all packages')

    # Add common arguments
    add_common_arguments(parser)

    args = parser.parse_args()

    # Determine enabled publications from args
    enabled_publications: Optional[List[str]] = None
    
    if args.list:
        enabled_publications = None
    elif args.all:
        enabled_publications = ["all"]
    else:
        # No specific action, create temporary publisher for interactive selection
        temp_publisher = NpmPublisher()
        enabled_publications = _select_publications_interactive(temp_publisher)
        if enabled_publications is None:
            return 0  # User cancelled
    
    # Create publisher with enabled publications and run
    publisher = NpmPublisher(enabled_publications=enabled_publications)
    return publisher.run(args)


def run_npm_publish(github_user: str, github_token: str, action: str = "all") -> bool:
    """
    Run NPM publish functionality programmatically
    
    This is the main entry point called by publish-all.py
    
    Args:
        github_user: GitHub username/organization
        github_token: GitHub access token
        action: Action to perform (list, all)
        
    Returns:
        True if successful, False otherwise
    """
    # Map action to enabled publications
    action_to_publications = {
        "list": None,
        "all": ["all"]
    }
    
    enabled_publications = action_to_publications.get(action)
    
    # Create a mock args object
    class MockArgs:
        def __init__(self, list_flag=False, user="", token=""):
            self.list = list_flag
            self.user = user
            self.token = token

    args = MockArgs(
        list_flag=(action == "list"),
        user=github_user,
        token=github_token
    )

    publisher = NpmPublisher(enabled_publications=enabled_publications)
    publisher.github_user = github_user
    publisher.github_token = github_token

    return publisher.run(args) == 0


if __name__ == '__main__':
    sys.exit(main())
