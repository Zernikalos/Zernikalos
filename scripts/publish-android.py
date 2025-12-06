#!/usr/bin/env python3
"""
Zernikalos Android Publish Script
Publishes Android artifacts to GitHub Packages Maven Repository

Python script for publishing Android libraries to GitHub Packages
"""

import os
import sys
import subprocess
import argparse
from pathlib import Path
from typing import Optional, List, Tuple
from common import BaseScript, add_common_arguments


class AndroidPublisher(BaseScript):
    """Main class for Android artifact publishing functionality"""
    
    def __init__(self):
        super().__init__("Zernikalos Android Publisher")
        
    def build_project(self) -> bool:
        """Build the Android project"""
        self.print_status("Building Android project...")
        return self.gradle.build()
            
    def check_build_directory(self) -> bool:
        """Check if build directory exists"""
        build_dir = self.project_root / "build"
        if not build_dir.exists():
            self.print_warning("Build directory 'build' does not exist")
            self.print_status("Building project automatically...")
            return self.build_project()
        return True
        
            
    def publish_android_debug(self) -> bool:
        """Publish Android Debug artifacts to GitHub Packages"""
        self.print_status("Publishing Android Debug artifacts...")
        
        success = self.gradle.publish_android_debug(self.github_user, self.github_token)
        if success:
            self.print_success("Android Debug artifacts published successfully!")
        return success
            
    def publish_android_release(self) -> bool:
        """Publish Android Release artifacts to GitHub Packages"""
        self.print_status("Publishing Android Release artifacts...")
        
        success = self.gradle.publish_android_release(self.github_user, self.github_token)
        if success:
            self.print_success("Android Release artifacts published successfully!")
        return success
            
    def publish_all_android(self) -> bool:
        """Publish both Android Debug and Release artifacts"""
        self.print_status("Publishing all Android artifacts...")
        
        success = True
        
        # Publish Debug
        if not self.publish_android_debug():
            success = False
            
        print()  # Add spacing between publications
        
        # Publish Release
        if not self.publish_android_release():
            success = False
            
        return success
        
    def publish_all_publications(self) -> bool:
        """Publish ALL publications to Maven Repository (recommended)"""
        self.print_status("Publishing ALL publications to Maven Repository...")
        
        self.print_status("This will publish:")
        self.print_status("  - dev.zernikalos:zernikalos (Android)")
        self.print_status("  - dev.zernikalos:zernikalos-js (JavaScript/Kotlin)")
        self.print_status("  - Any other configured publications")
        
        success = self.gradle.publish_all_publications(self.github_user, self.github_token)
        if success:
            self.print_success("ALL publications published successfully to Maven Repository!")
        return success
        
    def show_publication_info(self):
        """Show information about where artifacts are published"""
        version = self.get_project_version()
        if not version:
            return
            
        self.print_status("Publication Information:")
        print(f"  - Group ID: dev.zernikalos")
        print(f"  - Artifact ID: zernikalos")
        print(f"  - Version: {version}")
        print(f"  - Repository: https://maven.pkg.github.com/Zernikalos/Zernikalos")
        print(f"  - Maven coordinates:")
        print(f"    <dependency>")
        print(f"      <groupId>dev.zernikalos</groupId>")
        print(f"      <artifactId>zernikalos</artifactId>")
        print(f"      <version>{version}</version>")
        print(f"    </dependency>")
        
    def run(self, args):
        """Main execution method"""
        # Set credentials from command line if provided
        self.set_credentials_from_args(args)
        
        # Check prerequisites
        if not self.check_directory():
            return 1
            
        if not self.check_gradle():
            return 1
            
        # Get credentials
        if not self.get_github_credentials():
            return 1
            
        # Auto-build if needed
        if not self.check_build_directory():
            return 1
            
        # Get project version
        version = self.get_project_version()
        if not version:
            return 1
            
        # Execute action
        if args.debug:
            return 0 if self.publish_android_debug() else 1
            
        elif args.release:
            return 0 if self.publish_android_release() else 1
            
        elif args.all:
            return 0 if self.publish_all_android() else 1
            
        elif args.all_publications:
            return 0 if self.publish_all_publications() else 1
            
        elif args.info:
            self.show_publication_info()
            return 0
            
        else:
            # Default action: show info and ask what to publish
            self.show_publication_info()
            print()
            
            try:
                response = input("What would you like to publish? (debug/release/all/info): ").lower().strip()
                
                if response in ['debug', 'd']:
                    return 0 if self.publish_android_debug() else 1
                elif response in ['release', 'r']:
                    return 0 if self.publish_android_release() else 1
                elif response in ['all', 'a']:
                    return 0 if self.publish_all_android() else 1
                elif response in ['all_publications', 'ap', 'allpub']:
                    return 0 if self.publish_all_publications() else 1
                elif response in ['info', 'i']:
                    self.show_publication_info()
                    return 0
                else:
                    self.print_warning("Invalid option. Use: debug, release, all, all_publications, or info")
                    return 0
                    
            except KeyboardInterrupt:
                print()
                self.print_warning("Operation cancelled by user")
                return 0


def main():
    """Main entry point"""
    parser = argparse.ArgumentParser(
        description="Zernikalos Android Publish Script - Python version",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  python publish-android.py                                    # Interactive mode
  python publish-android.py -i                                 # Show publication info
  python publish-android.py -d                                 # Publish Debug artifacts
  python publish-android.py -r                                 # Publish Release artifacts
  python publish-android.py -a                                 # Publish all Android artifacts (Debug + Release)
  python publish-android.py --all-publications                 # Publish ALL publications (recommended)
  python publish-android.py -u Zernikalos -t TOKEN --all-publications  # With custom credentials

Prerequisites:
  1. Ensure you have Gradle wrapper (gradlew) available
  2. GitHub credentials (one of these methods):
     - Environment variables: GITHUB_ACTOR and GITHUB_TOKEN
     - Command line: -u USER -t TOKEN
     - Interactive prompt (only token required, user defaults to Zernikalos)
  3. The script will automatically:
     - Build the project if needed
     - Publish Android artifacts to GitHub Packages Maven Repository

Published artifacts:
  - Debug: Android Debug library artifacts
  - Release: Android Release library artifacts
  - All Publications: Complete Maven repository with all artifacts
  - Repository: https://maven.pkg.github.com/Zernikalos/Zernikalos
        """
    )
    
    parser.add_argument('-d', '--debug', action='store_true',
                       help='Publish Android Debug artifacts')
    parser.add_argument('-r', '--release', action='store_true',
                       help='Publish Android Release artifacts')
    parser.add_argument('-a', '--all', action='store_true',
                       help='Publish all Android artifacts (Debug + Release)')
    parser.add_argument('--all-publications', action='store_true',
                       help='Publish ALL publications to Maven Repository (recommended)')
    parser.add_argument('-i', '--info', action='store_true',
                       help='Show publication information')
    
    # Add common arguments
    add_common_arguments(parser)
    
    args = parser.parse_args()
    
    # Create publisher and run
    publisher = AndroidPublisher()
    return publisher.run(args)


def run_android_publish(github_user: str, github_token: str, action: str = "info") -> bool:
    """Run Android publish functionality programmatically"""
    # Create a mock args object
    class MockArgs:
        def __init__(self, debug=False, release=False, all=False, all_publications=False, info=False, user="", token=""):
            self.debug = debug
            self.release = release
            self.all = all
            self.all_publications = all_publications
            self.info = info
            self.user = user
            self.token = token
    
    args = MockArgs(
        debug=(action == "debug"),
        release=(action == "release"),
        all=(action == "all"),
        all_publications=(action == "all_publications"),
        info=(action == "info"),
        user=github_user,
        token=github_token
    )
    
    publisher = AndroidPublisher()
    if args.user:
        publisher.github_user = args.user
    if args.token:
        publisher.github_token = args.token
    
    return publisher.run(args) == 0


if __name__ == '__main__':
    sys.exit(main())
