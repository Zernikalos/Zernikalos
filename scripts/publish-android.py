#!/usr/bin/env python3
"""
Zernikalos Android Publish Script
Publishes Android artifacts to GitHub Packages Maven Repository

Python script for publishing Android libraries to GitHub Packages
"""

import sys
import argparse
from pathlib import Path
from typing import Optional, List, Tuple, Any
from common import add_common_arguments
from base_builder import BaseBuilder


class AndroidPublisher(BaseBuilder):
    """Main class for Android artifact publishing functionality"""
    
    def __init__(self):
        super().__init__("Zernikalos Android Publisher")
    
    def authentication(self) -> bool:
        """Setup and verify authentication"""
        return self.get_github_credentials()
    
    def build(self) -> bool:
        """Build the Android project"""
        self.print_status("Building Android project...")
        return self.gradle.build()
    
    def build_verify(self) -> bool:
        """Verify that build directory exists and artifacts are ready"""
        build_dir = self.project_root / "build"
        if not build_dir.exists():
            self.print_warning("Build directory 'build' does not exist")
            self.print_status("Building project automatically...")
            return self.build()
        return True
    
    def publish(self, variant: str = "all_publications", *args, **kwargs) -> bool:
        """
        Publish artifacts to the repository
        
        Args:
            variant: Publication variant (debug, release, all, all_publications)
            *args: Variable positional arguments
            **kwargs: Variable keyword arguments
            
        Returns:
            True if publish is successful, False otherwise
        """
        if variant == "debug":
            return self._publish_debug()
        elif variant == "release":
            return self._publish_release()
        elif variant == "all":
            return self._publish_all_android()
        elif variant == "all_publications":
            return self._publish_all_publications()
        else:
            self.print_error(f"Unknown publish variant: {variant}")
            return False
    
    def get_publish_info(self) -> bool:
        """Get and display publication information"""
        version = self.get_project_version()
        if not version:
            return False
        
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
        return True
    
    def _check_tool(self) -> bool:
        """Check if Gradle is available"""
        return self.check_gradle()
    
    def _get_default_action(self) -> int:
        """Get the default action when no specific action is provided"""
        self.get_publish_info()
        print()
        
        try:
            response = input("What would you like to publish? (debug/release/all/all_publications/info): ").lower().strip()
            
            if response in ['debug', 'd']:
                return 0 if self.publish("debug") else 1
            elif response in ['release', 'r']:
                return 0 if self.publish("release") else 1
            elif response in ['all', 'a']:
                return 0 if self.publish("all") else 1
            elif response in ['all_publications', 'ap', 'allpub']:
                return 0 if self.publish("all_publications") else 1
            elif response in ['info', 'i']:
                self.get_publish_info()
                return 0
            else:
                self.print_warning("Invalid option. Use: debug, release, all, all_publications, or info")
                return 0
                
        except KeyboardInterrupt:
            print()
            self.print_warning("Operation cancelled by user")
            return 0
    
    def _handle_action(self, args: Any) -> int:
        """Handle specific action based on command line arguments"""
        if args.debug:
            return 0 if self.publish("debug") else 1
        elif args.release:
            return 0 if self.publish("release") else 1
        elif args.all:
            return 0 if self.publish("all") else 1
        elif args.all_publications:
            return 0 if self.publish("all_publications") else 1
        elif args.info:
            return 0 if self.get_publish_info() else 1
        else:
            return self._get_default_action()
    
    # Private methods for internal publishing operations
    def _publish_debug(self) -> bool:
        """Publish Android Debug artifacts to GitHub Packages"""
        self.print_status("Publishing Android Debug artifacts...")
        
        success = self.gradle.publish_android_debug(self.github_user, self.github_token)
        if success:
            self.print_success("Android Debug artifacts published successfully!")
        return success
    
    def _publish_release(self) -> bool:
        """Publish Android Release artifacts to GitHub Packages"""
        self.print_status("Publishing Android Release artifacts...")
        
        success = self.gradle.publish_android_release(self.github_user, self.github_token)
        if success:
            self.print_success("Android Release artifacts published successfully!")
        return success
    
    def _publish_all_android(self) -> bool:
        """Publish both Android Debug and Release artifacts"""
        self.print_status("Publishing all Android artifacts...")
        
        success = True
        
        # Publish Debug
        if not self._publish_debug():
            success = False
        
        print()  # Add spacing between publications
        
        # Publish Release
        if not self._publish_release():
            success = False
        
        return success
    
    def _publish_all_publications(self) -> bool:
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
