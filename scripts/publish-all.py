#!/usr/bin/env python3
"""
Zernikalos All Publish Script
Publishes both NPM packages and Android artifacts to GitHub Packages

Combined script for publishing all Zernikalos artifacts
"""

import os
import sys
import argparse
from pathlib import Path
from typing import Optional, List, Tuple
from common import BaseScript, add_common_arguments
from publishers import run_npm_publish, run_android_publish


class AllPublisher(BaseScript):
    """Main class for publishing all Zernikalos artifacts"""
    
    def __init__(self):
        super().__init__("Zernikalos All Publisher")
        
    def publish_npm_packages(self) -> bool:
        """Publish NPM packages"""
        self.print_header("PUBLISHING NPM PACKAGES")
        
        # Verify credentials
        if not self.github_user or not self.github_token:
            self.print_error("GitHub credentials are required")
            return False
        
        # Verify npm is available
        if not self.check_npm():
            return False
        
        try:
            self.print_status("Running NPM publish...")
            success = run_npm_publish(
                github_user=self.github_user,
                github_token=self.github_token,
                action="all"
            )
            
            if success:
                self.print_success("NPM packages published successfully!")
                return True
            else:
                self.print_error("Failed to publish NPM packages")
                return False
                
        except Exception as e:
            self.print_error(f"Failed to publish NPM packages: {e}")
            return False
            
    def publish_android_artifacts(self) -> bool:
        """Publish Android artifacts"""
        self.print_header("PUBLISHING ANDROID ARTIFACTS")
        
        # Verify credentials
        if not self.github_user or not self.github_token:
            self.print_error("GitHub credentials are required")
            return False
        
        # Verify gradle is available
        if not self.check_gradle():
            return False
        
        try:
            self.print_status("Running Android publish...")
            success = run_android_publish(
                github_user=self.github_user,
                github_token=self.github_token,
                action="all_publications"
            )
            
            if success:
                self.print_success("Android artifacts published successfully!")
                return True
            else:
                self.print_error("Failed to publish Android artifacts")
                return False
                
        except Exception as e:
            self.print_error(f"Failed to publish Android artifacts: {e}")
            return False
            
    def publish_all(self) -> bool:
        """Publish all artifacts (NPM + Android)"""
        success = True
        
        # Publish NPM packages
        if not self.publish_npm_packages():
            success = False
            
        # Publish Android artifacts
        if not self.publish_android_artifacts():
            success = False
            
        # Final summary
        self.print_header("ZERNIKALOS COMPLETE PUBLISH")
        if success:
            self.print_success("All artifacts published successfully!")
            self.print_status("NPM packages: ✅ Published to GitHub Packages")
            self.print_status("Android artifacts: ✅ Published to GitHub Packages Maven Repository")
        else:
            self.print_error("Some artifacts failed to publish")
            
        return success
        
    def show_info(self) -> bool:
        """Show detailed information about available packages and artifacts"""
        self.print_header("DETAILED PROJECT INFORMATION")
        
        # Show NPM package info
        try:
            self.print_status("NPM Packages Information:")
            run_npm_publish(self.github_user, self.github_token, "list")
        except Exception as e:
            self.print_warning(f"Could not get NPM info: {e}")
            
        print()  # Add spacing
        
        # Show Android artifact info
        try:
            self.print_status("Android Artifacts Information:")
            run_android_publish(self.github_user, self.github_token, "info")
        except Exception as e:
            self.print_warning(f"Could not get Android info: {e}")
            
        return True
        
    def show_status(self):
        """Show current project status and available artifacts"""
        self.print_header("ZERNIKALOS PROJECT STATUS")
        
        # Check project version
        version_file = self.project_root / "VERSION.txt"
        if version_file.exists():
            version = version_file.read_text().strip()
            self.print_status(f"Project version: {version}")
        else:
            self.print_warning("VERSION.txt not found")
            
        # Check build directories
        js_build = self.project_root / "build" / "js"
        android_build = self.project_root / "build"
        
        print(f"\nBuild directories:")
        print(f"  - JavaScript: {'✅' if js_build.exists() else '❌'} {js_build}")
        print(f"  - Android: {'✅' if android_build.exists() else '❌'} {android_build}")
        
        # Check tools status
        gradle_status = self.gradle.check_status()
        npm_status = self.npm.check_status()
        
        print(f"\nTools status:")
        print(f"  - Gradle: {'✅' if gradle_status['available'] else '❌'} {gradle_status['message']}")
        if gradle_status['available'] and gradle_status['version']:
            print(f"    Path: {gradle_status['gradlew_path']}")
        
        print(f"  - npm: {'✅' if npm_status['available'] else '❌'} {npm_status['message']}")
        
        # Check credentials
        print(f"\nGitHub credentials:")
        print(f"  - User: {'✅' if self.github_user else '❌'} {self.github_user or 'Not set'}")
        print(f"  - Token: {'✅' if self.github_token else '❌'} {'Set' if self.github_token else 'Not set'}")
        
    def run(self, args):
        """Main execution method"""
        # Set credentials from command line if provided
        self.set_credentials_from_args(args)
        
        # Check prerequisites
        if not self.check_directory():
            return 1
        
        # For info/status commands, credentials might not be needed
        if not (args.status or args.info):
            # Get credentials for publishing operations
            if not self.get_github_credentials():
                return 1
            
        # Execute action
        if args.status:
            self.show_status()
            return 0
            
        elif args.npm:
            return 0 if self.publish_npm_packages() else 1
            
        elif args.android:
            return 0 if self.publish_android_artifacts() else 1
            
        elif args.all:
            return 0 if self.publish_all() else 1
            
        elif args.info:
            return 0 if self.show_info() else 1
            
        else:
            # Default action: show status and ask what to publish
            self.show_status()
            print()
            
            try:
                response = input("What would you like to publish? (npm/android/all): ").lower().strip()
                
                if response in ['npm', 'n']:
                    return 0 if self.publish_npm_packages() else 1
                elif response in ['android', 'a']:
                    return 0 if self.publish_android_artifacts() else 1
                elif response in ['all', 'complete']:
                    return 0 if self.publish_all() else 1
                else:
                    self.print_warning("Invalid option. Use: npm, android, or all")
                    return 0
                    
            except KeyboardInterrupt:
                print()
                self.print_warning("Operation cancelled by user")
                return 0


def main():
    """Main entry point"""
    parser = argparse.ArgumentParser(
        description="Zernikalos All Publish Script - Complete publishing solution",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  python publish-all.py                                    # Interactive mode
  python publish-all.py -s                                 # Show project status
  python publish-all.py -i                                 # Show detailed package info
  python publish-all.py -n                                 # Publish NPM packages only
  python publish-all.py -a                                 # Publish Android artifacts only
  python publish-all.py --all                              # Publish everything
  python publish-all.py -u Zernikalos -t TOKEN --all       # With custom credentials

Prerequisites:
  1. GitHub credentials (one of these methods):
     - Environment variables: GITHUB_ACTOR/GITHUB_USER and GITHUB_TOKEN
     - Command line: -u USER -t TOKEN
     - Interactive prompt (only token required, user defaults to Zernikalos)
  3. Required tools:
     - npm (for NPM publishing)
     - Gradle wrapper (for Android publishing)
  4. The script will automatically:
     - Verify credentials and tools before publishing
     - Publish NPM packages to GitHub Packages
     - Publish Android artifacts to GitHub Packages Maven Repository

Published artifacts:
  - NPM: JavaScript packages for web/browser usage
  - Android: Debug and Release library artifacts for Android development
        """
    )
    
    parser.add_argument('-s', '--status', action='store_true',
                       help='Show project status and available artifacts')
    parser.add_argument('-i', '--info', action='store_true',
                       help='Show detailed information about packages and artifacts')
    parser.add_argument('-n', '--npm', action='store_true',
                       help='Publish NPM packages only')
    parser.add_argument('-a', '--android', action='store_true',
                       help='Publish Android artifacts only')
    parser.add_argument('--all', action='store_true',
                       help='Publish all artifacts (NPM + Android)')
    
    # Add common arguments
    add_common_arguments(parser)
    
    args = parser.parse_args()
    
    # Create publisher and run
    publisher = AllPublisher()
    return publisher.run(args)


if __name__ == '__main__':
    sys.exit(main())
