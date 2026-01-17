"""
Version CLI Handler
Command-line interface for version management commands
"""

import argparse
from pathlib import Path
from common import BaseScript, validate_version
from versioning import VersionManager
from versioning.types import VersionInfo
from tools import VersionBump


def add_version_arguments(parser: argparse.ArgumentParser) -> None:
    """Add version subcommand arguments"""
    version_group = parser.add_mutually_exclusive_group()
    version_group.add_argument('version', nargs='?', help='Version to release (format: X.Y.Z)')
    version_group.add_argument('--auto', action='store_true',
                               help='Automatically calculate version from Conventional Commits')
    parser.add_argument('--no-push', action='store_true',
                       help='Create local release without pushing to remote')
    parser.add_argument('--show-next', action='store_true',
                       help='Show calculated next version based on Conventional Commits')


def add_release_arguments(parser: argparse.ArgumentParser) -> None:
    """Add release subcommand arguments"""
    from .common_args import add_common_arguments
    
    release_version_group = parser.add_mutually_exclusive_group(required=True)
    release_version_group.add_argument('version', nargs='?', help='Version to release (format: X.Y.Z)')
    release_version_group.add_argument('--auto', action='store_true',
                                       help='Automatically calculate version from Conventional Commits')
    add_common_arguments(parser)
    parser.add_argument('--no-publish', action='store_true',
                       help='Only create version, do not publish')


def confirm_release(base: BaseScript, current_version: str, new_version: str, no_push: bool) -> bool:
    """Confirm release with user"""
    print()
    base.print_status(f"Current version: {current_version}")
    base.print_status(f"New version: {new_version}")
    print()
    base.print_warning("This will:")
    print(f"  1. Update VERSION.txt to {new_version}")
    print("  2. Upgrade Kotlin package lock")
    print("  3. Generate version constants")
    print("  4. Create release commit")
    print(f"  5. Create git tag v{new_version}")
    
    if no_push:
        print("  6. [SKIP] Push changes (--no-push flag detected)")
        base.print_warning("This is a LOCAL release only. No CI/CD will be triggered.")
    else:
        print("  6. Push changes and tag to trigger CI/CD")
    print()
    
    return base.confirm_action("Proceed with release?", default=False)


def show_next_steps(base: BaseScript, version: str) -> None:
    """Show next steps after successful release"""
    print()
    base.print_success(f"🎉 Release v{version} completed successfully!")
    print()
    base.print_status("Next steps:")
    print("  1. Monitor the GitHub Actions workflow:")
    print("     https://github.com/Zernikalos/Zernikalos/actions")
    print("  2. Check the release workflow:")
    print("     https://github.com/Zernikalos/Zernikalos/actions/workflows/release.yml")
    print("  3. Verify packages are published:")
    print("     - Maven: https://maven.pkg.github.com/Zernikalos/Zernikalos")
    print("     - NPM: https://npm.pkg.github.com/@zernikalos/zernikalos")
    print()
    base.print_status("The release will be automatically published once the build completes.")


def show_local_release_info(base: BaseScript, version: str) -> None:
    """Show information for local release"""
    print()
    base.print_success(f"🎉 Local Release v{version} completed successfully!")
    print()
    base.print_status("This was a LOCAL release (--no-push flag used).")
    base.print_status("To publish this release to GitHub:")
    print()
    print("  1. Review the changes:")
    print("     git log --oneline -5")
    print(f"     git show v{version}")
    print()
    print("  2. Push when ready:")
    print("     git push origin main")
    print(f"     git push origin v{version}")
    print()
    base.print_warning("No CI/CD pipeline was triggered. This is just a local preparation.")


def show_next_version_info(base: BaseScript, version_info: VersionInfo) -> None:
    """Show calculated next version information"""
    base.print_header("NEXT VERSION CALCULATION")
    
    base.print_status(f"Base version (VERSION.txt): {version_info.base_version}")
    
    if version_info.last_tag:
        base.print_status(f"Last release tag: {version_info.last_tag}")
    else:
        base.print_warning("No release tags found, using v0.0.0 as base")
    
    print()
    base.print_status("Commits analyzed:")
    if version_info.commits:
        from tools import ConventionalCommitsTool
        commit_analyzer = ConventionalCommitsTool(base.project_root)
        
        for commit in version_info.commits[:10]:  # Show first 10 commits
            commit_type = commit_analyzer.analyze_commit_type(commit)
            type_str = commit_type.value.upper() if commit_type else "OTHER"
            print(f"  [{type_str:6}] {commit}")
        if len(version_info.commits) > 10:
            print(f"  ... and {len(version_info.commits) - 10} more commits")
    else:
        base.print_warning("  No commits found since last tag")
    
    print()
    base.print_status("Version calculation:")
    print(f"  Bump type: {version_info.bump_type.value.upper()}")
    print(f"  Next version: {version_info.next_version}")
    print()
    base.print_success("Versions for publishing:")
    print(f"  Maven: {version_info.maven_version}")
    print(f"  npm:   {version_info.npm_version}")


def handle_version_command(args, project_root: Path = None) -> int:
    """
    Handle version subcommand
    
    Args:
        args: Parsed command line arguments
        project_root: Root directory of the project
        
    Returns:
        Exit code (0 for success, 1 for failure)
    """
    base = BaseScript("Zernikalos Version Manager")
    if project_root:
        base.project_root = project_root
    
    manager = VersionManager(project_root)
    
    # Handle --show-next flag
    if args.show_next:
        version_info = manager.calculate_next_version()
        if not version_info:
            base.print_error("Could not calculate next version")
            return 1
        
        show_next_version_info(base, version_info)
        return 0
    
    # Determine version (either from --auto or explicit version)
    if args.auto:
        # Calculate version automatically
        version_info = manager.calculate_next_version()
        if not version_info:
            base.print_error("Failed to calculate next version")
            return 1
        version = version_info.next_version
        base.print_status(f"Auto-calculated version: {version}")
    else:
        # Use explicit version
        if not args.version:
            base.print_error("version is required unless --auto or --show-next is used")
            return 1
        
        if not validate_version(args.version):
            base.print_error("Invalid version format. Use X.Y.Z (e.g., 0.4.1)")
            return 1
        
        version = args.version
    
    # Validate and check prerequisites
    validation = manager.validate_release(version)
    if not validation.is_valid:
        if validation.error_message:
            base.print_error(validation.error_message)
        return 1
    
    # Get current version for confirmation
    current_version = manager.get_current_version()
    if not current_version:
        base.print_error("Could not read VERSION.txt")
        return 1
    
    # Confirm release
    if not confirm_release(base, current_version, version, args.no_push):
        base.print_error("Release cancelled")
        return 1
    
    # Execute release
    base.print_status(f"Starting release process for version {version}...")
    release_result = manager.execute_release(version)
    
    if not release_result.success:
        base.print_error(f"Release failed: {release_result.error_message}")
        if release_result.steps_completed:
            base.print_status(f"Completed steps: {', '.join(release_result.steps_completed)}")
        return 1
    
    # Show progress for each step
    for step in release_result.steps_completed:
        if step == "set_version":
            base.print_status(f"✓ Set version to {version}")
        elif step == "upgrade_kotlin_package_lock":
            base.print_status("✓ Upgraded Kotlin package lock")
        elif step == "update_version":
            base.print_status("✓ Generated version-dependent files")
        elif step == "release_commit":
            base.print_status("✓ Created release commit and tag")
    
    base.print_success("Release files generated successfully!")
    
    # Push changes (if not --no-push)
    if not args.no_push:
        base.print_status("Pushing changes to remote repository...")
        push_result = manager.push_release(version)
        
        if not push_result.success:
            base.print_error(f"Failed to push: {push_result.error_message}")
            return 1
        
        base.print_success("Changes pushed successfully!")
        base.print_success(f"CI/CD pipeline will now build and publish version {version}")
        show_next_steps(base, version)
    else:
        show_local_release_info(base, version)
    
    return 0


def handle_release_command(args, project_root: Path = None) -> int:
    """Handle release subcommand (version + optional publish)"""
    base = BaseScript("Zernikalos Release Manager")
    if project_root:
        base.project_root = project_root
    
    if not base.check_directory():
        return 1
    
    manager = VersionManager(project_root)
    
    # Determine version
    if args.auto:
        # Calculate version automatically
        version_info = manager.calculate_next_version()
        if not version_info:
            base.print_error("Failed to calculate next version")
            return 1
        version = version_info.next_version
        base.print_status(f"Auto-calculated version: {version}")
        
        # Get current version for confirmation
        current_version = manager.get_current_version()
        if not current_version:
            base.print_error("Could not read VERSION.txt")
            return 1
        
        # Confirm with user (using CLI helper)
        if not confirm_release(base, current_version, version, False):
            base.print_error("Release cancelled")
            return 1
    else:
        if not args.version:
            base.print_error("version is required unless --auto is used")
            return 1
        
        if not validate_version(args.version):
            base.print_error("Invalid version format. Use X.Y.Z (e.g., 0.4.1)")
            return 1
        
        version = args.version
        
        # Get current version for confirmation
        current_version = manager.get_current_version()
        if not current_version:
            base.print_error("Could not read VERSION.txt")
            return 1
        
        # Confirm with user
        if not confirm_release(base, current_version, version, False):
            base.print_error("Release cancelled")
            return 1
    
    # Validate and check prerequisites
    validation = manager.validate_release(version)
    if not validation.is_valid:
        if validation.error_message:
            base.print_error(validation.error_message)
        return 1
    
    # Execute version release
    base.print_status(f"Starting release process for version {version}...")
    release_result = manager.execute_release(version)
    
    if not release_result.success:
        base.print_error(f"Release failed: {release_result.error_message}")
        if release_result.steps_completed:
            base.print_status(f"Completed steps: {', '.join(release_result.steps_completed)}")
        return 1
    
    # Show progress for each step
    for step in release_result.steps_completed:
        if step == "set_version":
            base.print_status(f"✓ Set version to {version}")
        elif step == "upgrade_kotlin_package_lock":
            base.print_status("✓ Upgraded Kotlin package lock")
        elif step == "update_version":
            base.print_status("✓ Generated version-dependent files")
        elif step == "release_commit":
            base.print_status("✓ Created release commit and tag")
    
    base.print_success("Release files generated successfully!")
    
    # Push changes
    base.print_status("Pushing changes to remote repository...")
    push_result = manager.push_release(version)
    
    if not push_result.success:
        base.print_error(f"Failed to push: {push_result.error_message}")
        return 1
    
    base.print_success("Changes pushed successfully!")
    base.print_success(f"CI/CD pipeline will now build and publish version {version}")
    
    # Publish (if not --no-publish)
    if not args.no_publish:
        from publishing import PublisherManager
        from cli.publisher_cli import get_credentials_from_args
        
        credentials = get_credentials_from_args(args, base)
        if not credentials:
            base.print_warning("Skipping publish due to missing credentials")
        else:
            base.print_status("Publishing artifacts...")
            publisher_manager = PublisherManager(base.project_root)
            result = publisher_manager.publish_all(credentials)
            if not result.success:
                base.print_warning("Some artifacts failed to publish")
    
    show_next_steps(base, version)
    return 0

