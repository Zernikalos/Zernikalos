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

# Import the publish functions from other scripts
try:
    import importlib.util
    scripts_dir = Path(__file__).parent
    
    # Load publish-npm module
    npm_spec = importlib.util.spec_from_file_location("publish_npm", scripts_dir / "publish-npm.py")
    npm_module = importlib.util.module_from_spec(npm_spec)
    npm_spec.loader.exec_module(npm_module)
    run_npm_publish = npm_module.run_npm_publish
    
    # Load publish-android module
    android_spec = importlib.util.spec_from_file_location("publish_android", scripts_dir / "publish-android.py")
    android_module = importlib.util.module_from_spec(android_spec)
    android_spec.loader.exec_module(android_module)
    run_android_publish = android_module.run_android_publish
    
except ImportError:
    # Fallback if direct import fails (when running from different directory)
    def load_module_from_path(script_path: str):
        """Load a Python module from a file path"""
        spec = importlib.util.spec_from_file_location("module", script_path)
        module = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(module)
        return module
    
    def get_publish_functions():
        """Get publish functions from script files"""
        scripts_dir = Path(__file__).parent
        npm_module = load_module_from_path(scripts_dir / "publish-npm.py")
        android_module = load_module_from_path(scripts_dir / "publish-android.py")
        return npm_module.run_npm_publish, android_module.run_android_publish
    
    run_npm_publish, run_android_publish = get_publish_functions()


class AllPublisher(BaseScript):
    """Main class for publishing all Zernikalos artifacts"""
    
    def __init__(self):
        super().__init__("Zernikalos All Publisher")
        
    def publish_npm_packages(self) -> bool:
        """Publish NPM packages using the publish-npm.py script"""
        self.print_header("PUBLISHING NPM PACKAGES")
        
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
        """Publish Android artifacts using the publish-android.py script"""
        self.print_header("PUBLISHING ANDROID ARTIFACTS")
        
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
        self.print_header("ZERNIKALOS COMPLETE PUBLISH")
        
        success = True
        
        # Publish NPM packages
        if not self.publish_npm_packages():
            success = False
            
        # Publish Android artifacts
        if not self.publish_android_artifacts():
            success = False
            
        # Final summary
        self.print_header("PUBLISH SUMMARY")
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
        
        # Check scripts availability
        try:
            run_npm_publish("test", "test", "list")
            npm_available = "✅ Available"
        except Exception:
            npm_available = "❌ Not available"
            
        try:
            run_android_publish("test", "test", "info")
            android_available = "✅ Available"
        except Exception:
            android_available = "❌ Not available"
        
        print(f"\nPublish functions:")
        print(f"  - NPM: {npm_available}")
        print(f"  - Android: {android_available}")
        
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
            
        # Get credentials
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
                response = input("What would you like to publish? (npm/android/all/status): ").lower().strip()
                
                if response in ['npm', 'n']:
                    return 0 if self.publish_npm_packages() else 1
                elif response in ['android', 'a']:
                    return 0 if self.publish_android_artifacts() else 1
                elif response in ['all', 'complete']:
                    return 0 if self.publish_all() else 1
                elif response in ['status', 's', 'info', 'i']:
                    self.show_status()
                    return 0
                else:
                    self.print_warning("Invalid option. Use: npm, android, all, or status")
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
  1. Both publish-npm.py and publish-android.py scripts must be available
  2. GitHub credentials (one of these methods):
     - Environment variables: GITHUB_ACTOR/GITHUB_USER and GITHUB_TOKEN
     - Command line: -u USER -t TOKEN
     - Interactive prompt (only token required, user defaults to Zernikalos)
  3. The script will automatically:
     - Use the appropriate publish script for each artifact type
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
