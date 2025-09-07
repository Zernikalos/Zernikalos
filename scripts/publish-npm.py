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
        
    def generate_npmrc(self) -> bool:
        """Generate .npmrc file with credentials"""
        self.print_status("Generating .npmrc file with credentials...")
        
        if not self.get_github_credentials():
            return False
            
        return self.run_gradle_command('generateNpmrc', 
                                     f'-Puser={self.github_user}',
                                     f'-Paccess_token={self.github_token}')
            
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
        
    def check_npmrc(self) -> bool:
        """Check if .npmrc exists"""
        npmrc_path = self.project_root / "build" / "js" / ".npmrc"
        if not npmrc_path.exists():
            self.print_warning(".npmrc file not found in build/js/")
            self.print_status("Generating .npmrc automatically...")
            return self.generate_npmrc()
            
        self.print_success(".npmrc file found and configured")
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
        
    def publish_package(self, package_name: str) -> bool:
        """Publish a specific package"""
        package_path = self.project_root / "build" / "js" / "packages" / "@zernikalos" / package_name
        
        if not package_path.exists():
            self.print_error(f"Package '{package_name}' not found")
            return False
            
        package_json = package_path / "package.json"
        if not package_json.exists():
            self.print_error(f"package.json not found in {package_path}")
            return False
            
        self.print_status(f"Publishing package: @zernikalos/{package_name}")
        
        # Get current version
        try:
            with open(package_json, 'r') as f:
                data = json.load(f)
                current_version = data.get('version', 'unknown')
        except (json.JSONDecodeError, IOError):
            self.print_error("Failed to read package version")
            return False
            
        self.print_status(f"Package version: {current_version}")
        
        # Confirm publication
        try:
            response = input(f"Proceed with publishing @zernikalos/{package_name} v{current_version}? (y/N): ")
            if response.lower() != 'y':
                self.print_warning("Publication cancelled")
                return True
        except KeyboardInterrupt:
            print()
            self.print_warning("Operation cancelled by user")
            return True
            
        # Publish the package
        self.print_status("Publishing to GitHub Packages...")
        
        try:
            # Change to package directory
            original_dir = os.getcwd()
            os.chdir(package_path)
            
            # Copy .npmrc from build/js to the package directory
            npmrc_source = self.project_root / "build" / "js" / ".npmrc"
            npmrc_dest = Path.cwd() / ".npmrc"
            
            if npmrc_source.exists():
                import shutil
                shutil.copy2(npmrc_source, npmrc_dest)
                self.print_status("Copied .npmrc to package directory")
            else:
                self.print_error(".npmrc not found in build/js/")
                os.chdir(original_dir)
                return False
            
            # Run npm publish with full output
            result = subprocess.run(['npm', 'publish'], capture_output=True, text=True)
            
            # Show npm output
            if result.stdout:
                print("npm output:")
                print(result.stdout)
            if result.stderr:
                print("npm errors:")
                print(result.stderr)
            
            if result.returncode == 0:
                self.print_success(f"Package @zernikalos/{package_name} v{current_version} published successfully!")
                self.print_status(f"Package available at: https://npm.pkg.github.com/@zernikalos/{package_name}")
                
                # Clean up .npmrc from package directory
                if npmrc_dest.exists():
                    npmrc_dest.unlink()
                    self.print_status("Cleaned up .npmrc from package directory")
                
                # Return to original directory
                os.chdir(original_dir)
                return True
            else:
                self.print_error(f"npm publish failed with exit code {result.returncode}")
                # Return to original directory
                os.chdir(original_dir)
                return False
            
        except Exception as e:
            self.print_error(f"Failed to publish package: {e}")
            # Return to original directory
            os.chdir(original_dir)
            return False
            
    def publish_all_packages(self) -> bool:
        """Publish all packages"""
        self.print_status("Publishing all packages...")
        
        packages = self.list_packages()
        if not packages:
            return False
            
        success = True
        for package_name, version in packages:
            print()
            if not self.publish_package(package_name):
                success = False
                
        return success
        
    def run(self, args):
        """Main execution method"""
        # Set credentials from command line if provided
        self.set_credentials_from_args(args)
        
        # Check prerequisites
        if not self.check_directory():
            return 1
            
        if not self.check_npm():
            return 1
            
        # Auto-generate and build if needed
        if not self.check_npmrc():
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
     - Generate .npmrc with credentials
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
