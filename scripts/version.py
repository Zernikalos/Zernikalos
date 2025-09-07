#!/usr/bin/env python3

"""
Zernikalos Version Script
Automates the complete version release process for X.Y.Z versions
"""

import sys
import argparse
from common import BaseScript, validate_version, check_git_status, check_version_exists, push_to_remote


class VersionManager(BaseScript):
    """Version management functionality"""
    
    def __init__(self):
        super().__init__("Zernikalos Version Manager")
        
    def confirm_release(self, version: str, no_push: bool) -> bool:
        """Confirm release with user"""
        current_version = self.get_project_version()
        if not current_version:
            return False
            
        print()
        self.print_status(f"Current version: {current_version}")
        self.print_status(f"New version: {version}")
        print()
        self.print_warning("This will:")
        print(f"  1. Update VERSION.txt to {version}")
        print("  2. Generate version constants")
        print("  3. Create release commit")
        print(f"  4. Create git tag v{version}")
        
        if no_push:
            print("  5. [SKIP] Push changes (--no-push flag detected)")
            self.print_warning("This is a LOCAL release only. No CI/CD will be triggered.")
        else:
            print("  5. Push changes and tag to trigger CI/CD")
        print()
        
        return self.confirm_action("Proceed with release?", default=False)

    def execute_release(self, version: str) -> bool:
        """Execute the release steps"""
        self.print_status(f"Starting release process for version {version}...")
        
        # Step 1: Set version
        self.print_status(f"Setting version to {version}...")
        if not self.run_gradle_command('setVersion', f'-PnewVersion={version}'):
            return False
        
        # Step 2: Generate version files
        self.print_status("Generating version-dependent files...")
        if not self.run_gradle_command('updateVersion'):
            return False
        
        # Step 3: Create release commit and tag
        self.print_status("Creating release commit and tag...")
        if not self.run_gradle_command('releaseCommit'):
            return False
        
        self.print_success("Release files generated successfully!")
        return True

    def push_release(self, version: str) -> bool:
        """Push changes to remote repository"""
        self.print_status("Pushing changes to remote repository...")
        
        if push_to_remote("main", f"v{version}"):
            self.print_success("Changes pushed successfully!")
            self.print_success(f"CI/CD pipeline will now build and publish version {version}")
            return True
        else:
            self.print_error("Failed to push changes")
            return False

    def show_next_steps(self, version: str) -> None:
        """Show next steps after successful release"""
        print()
        self.print_success(f"🎉 Release v{version} completed successfully!")
        print()
        self.print_status("Next steps:")
        print("  1. Monitor the GitHub Actions workflow:")
        print("     https://github.com/Zernikalos/Zernikalos/actions")
        print("  2. Check the release build:")
        print("     https://github.com/Zernikalos/Zernikalos/actions/workflows/build.yml")
        print("  3. Verify packages are published:")
        print("     - Maven: https://maven.pkg.github.com/Zernikalos/Zernikalos")
        print("     - NPM: https://npm.pkg.github.com/@zernikalos/zernikalos")
        print()
        self.print_status("The release will be automatically published once the build completes.")

    def show_local_release_info(self, version: str) -> None:
        """Show information for local release"""
        print()
        self.print_success(f"🎉 Local Release v{version} completed successfully!")
        print()
        self.print_status("This was a LOCAL release (--no-push flag used).")
        self.print_status("To publish this release to GitHub:")
        print()
        print("  1. Review the changes:")
        print("     git log --oneline -5")
        print(f"     git show v{version}")
        print()
        print("  2. Push when ready:")
        print("     git push origin main")
        print(f"     git push origin v{version}")
        print()
        self.print_warning("No CI/CD pipeline was triggered. This is just a local preparation.")

    def run(self, args):
        """Main execution method"""
        # Validate version format
        if not validate_version(args.version):
            self.print_error("Invalid version format. Use X.Y.Z (e.g., 0.4.1)")
            return 1
        
        # Check we're in the right directory
        if not self.check_directory():
            return 1
        
        # Check git status
        is_clean, git_output = check_git_status()
        if not is_clean:
            self.print_warning("Git working directory is not clean. Please commit or stash changes first.")
            print(git_output)
            if not self.confirm_action("Continue anyway?", default=False):
                self.print_error("Release cancelled")
                return 1
        
        # Check if version already exists
        if check_version_exists(args.version):
            self.print_error(f"Version v{args.version} already exists as a tag")
            return 1
        
        # Confirm release
        if not self.confirm_release(args.version, args.no_push):
            self.print_error("Release cancelled")
            return 1
        
        # Execute release
        if not self.execute_release(args.version):
            return 1
        
        # Push changes (if not --no-push)
        if not args.no_push:
            if not self.push_release(args.version):
                return 1
            self.show_next_steps(args.version)
        else:
            self.show_local_release_info(args.version)
        
        return 0


def main():
    """Main function"""
    parser = argparse.ArgumentParser(
        description="Zernikalos Version Release Script",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  python3 version.py 0.4.1
  python3 version.py 0.4.1 --no-push
        """
    )
    
    parser.add_argument('version', help='Version to release (format: X.Y.Z)')
    parser.add_argument('--no-push', action='store_true', 
                       help='Create local release without pushing to remote')
    
    args = parser.parse_args()
    
    # Create version manager and run
    version_manager = VersionManager()
    return version_manager.run(args)


if __name__ == "__main__":
    sys.exit(main())