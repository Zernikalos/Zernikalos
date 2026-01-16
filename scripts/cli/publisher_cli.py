"""
Publisher CLI Handler
Command-line interface for publishing commands
"""

import argparse
import os
import getpass
from pathlib import Path
from common import BaseScript
from publishing import PublisherManager
from publishing.base_builder import GitHubCredentials
from publishing.types import PublishResult, StatusInfo, PublishInfo


def add_publish_arguments(parser: argparse.ArgumentParser) -> None:
    """Add publish subcommand arguments"""
    from .common_args import add_common_arguments
    
    publish_group = parser.add_mutually_exclusive_group(required=True)
    publish_group.add_argument('--all', action='store_true',
                             help='Publish all artifacts (NPM + Maven)')
    publish_group.add_argument('--npm', action='store_true',
                             help='Publish NPM packages only')
    publish_group.add_argument('--maven', action='store_true',
                             help='Publish Maven artifacts only')
    add_common_arguments(parser)


def add_status_arguments(parser: argparse.ArgumentParser) -> None:
    """Add status subcommand arguments"""
    # No arguments needed for status, but function for consistency
    pass


def add_info_arguments(parser: argparse.ArgumentParser) -> None:
    """Add info subcommand arguments"""
    from .common_args import add_common_arguments
    add_common_arguments(parser)


def get_credentials_from_args(args, base: BaseScript) -> GitHubCredentials:
    """
    Get GitHub credentials from args or environment
    
    Args:
        args: Parsed command line arguments
        base: BaseScript instance for I/O
        
    Returns:
        GitHubCredentials instance
    """
    # Get user from args, env, or default
    user = None
    if hasattr(args, 'user') and args.user:
        user = args.user
    elif base.github_user:
        user = base.github_user
    else:
        user = os.environ.get('GITHUB_ACTOR') or os.environ.get('GITHUB_USER', 'Zernikalos')
    
    # Get token from args or env
    token = None
    if hasattr(args, 'token') and args.token:
        token = args.token
    elif base.github_token:
        token = base.github_token
    else:
        token = os.environ.get('GITHUB_TOKEN')
    
    # Prompt for token if not provided
    if not token:
        base.print_status("GitHub credentials required")
        base.print_status(f"Organization: {user}")
        try:
            token = getpass.getpass("Enter GitHub access token: ")
            if not token:
                base.print_error("GitHub access token is required")
                return None
        except KeyboardInterrupt:
            print()
            base.print_error("Operation cancelled by user")
            return None
    
    return GitHubCredentials(user=user, token=token)


def show_publish_result(base: BaseScript, result: PublishResult) -> None:
    """Display publish result to user"""
    if result.success:
        if result.target == 'npm':
            base.print_success("NPM packages published successfully!")
        elif result.target == 'maven':
            base.print_success("Maven artifacts published successfully!")
        elif result.target == 'all':
            base.print_success("All artifacts published successfully!")
            base.print_status("NPM packages: ✅ Published to GitHub Packages")
            base.print_status("Maven artifacts: ✅ Published to GitHub Packages Maven Repository")
    else:
        base.print_error(result.error_message or "Publish failed")
        if result.details:
            if result.target == 'all':
                if result.details.get('npm', {}).get('error'):
                    base.print_error(f"NPM error: {result.details['npm']['error']}")
                if result.details.get('maven', {}).get('error'):
                    base.print_error(f"Maven error: {result.details['maven']['error']}")


def show_status_info(base: BaseScript, status: StatusInfo) -> None:
    """Display status information to user"""
    base.print_header("ZERNIKALOS PROJECT STATUS")
    
    if status.version:
        base.print_status(f"Project version: {status.version}")
    else:
        if not status.version_file_exists:
            base.print_warning("VERSION.txt not found")
    
    print(f"\nBuild directories:")
    js_build = base.project_root / "build" / "js"
    android_build = base.project_root / "build"
    print(f"  - JavaScript: {'✅' if status.js_build_exists else '❌'} {js_build}")
    print(f"  - Android: {'✅' if status.android_build_exists else '❌'} {android_build}")
    
    print(f"\nTools status:")
    print(f"  - Gradle: {'✅' if status.gradle_available else '❌'} {status.gradle_version or 'Not available'}")
    if status.gradle_available and status.gradlew_path:
        print(f"    Path: {status.gradlew_path}")
    
    print(f"  - npm: {'✅' if status.npm_available else '❌'} {status.npm_version or 'Not available'}")
    
    print(f"\nGitHub credentials:")
    print(f"  - User: {'✅' if status.github_user else '❌'} {status.github_user or 'Not set'}")
    print(f"  - Token: {'✅' if status.github_token_set else '❌'} {'Set' if status.github_token_set else 'Not set'}")


def show_publish_info(base: BaseScript, info: PublishInfo) -> None:
    """Display publish information to user"""
    base.print_header("DETAILED PROJECT INFORMATION")
    
    if info.error_message:
        base.print_warning(info.error_message)
    
    # Show NPM package info
    base.print_status("NPM Packages Information:")
    # The actual info display is handled by the publisher's run() method
    # when enabled_publications is None
    
    print()  # Add spacing
    
    # Show Maven artifact info
    base.print_status("Maven Artifacts Information:")
    # The actual info display is handled by the publisher's run() method
    # when enabled_publications is None


def handle_publish_command(args, project_root: Path = None) -> int:
    """
    Handle publish subcommand
    
    Args:
        args: Parsed command line arguments
        project_root: Root directory of the project
        
    Returns:
        Exit code (0 for success, 1 for failure)
    """
    base = BaseScript("Zernikalos Publisher")
    if project_root:
        base.project_root = project_root
    
    if not base.check_directory():
        return 1
    
    # Get credentials
    credentials = get_credentials_from_args(args, base)
    if not credentials:
        return 1
    
    manager = PublisherManager(project_root)
    
    # Execute publish action
    if args.all:
        base.print_header("PUBLISHING ALL ARTIFACTS")
        result = manager.publish_all(credentials)
        show_publish_result(base, result)
        return 0 if result.success else 1
    elif args.npm:
        base.print_header("PUBLISHING NPM PACKAGES")
        result = manager.publish_npm(credentials)
        show_publish_result(base, result)
        return 0 if result.success else 1
    elif args.maven:
        base.print_header("PUBLISHING MAVEN ARTIFACTS")
        result = manager.publish_maven(credentials)
        show_publish_result(base, result)
        return 0 if result.success else 1
    else:
        base.print_error("No publish target specified")
        return 1


def handle_status_command(project_root: Path = None) -> int:
    """
    Handle status subcommand
    
    Args:
        project_root: Root directory of the project
        
    Returns:
        Exit code (0 for success, 1 for failure)
    """
    base = BaseScript("Zernikalos Publisher")
    if project_root:
        base.project_root = project_root
    
    if not base.check_directory():
        return 1
    
    manager = PublisherManager(project_root)
    status = manager.get_status()
    
    # Set credentials info if available
    if base.github_user:
        status.github_user = base.github_user
    if base.github_token:
        status.github_token_set = True
    
    show_status_info(base, status)
    return 0


def handle_info_command(args, project_root: Path = None) -> int:
    """
    Handle info subcommand
    
    Args:
        args: Parsed command line arguments
        project_root: Root directory of the project
        
    Returns:
        Exit code (0 for success, 1 for failure)
    """
    base = BaseScript("Zernikalos Publisher")
    if project_root:
        base.project_root = project_root
    
    if not base.check_directory():
        return 1
    
    # Get credentials (optional for info, but try anyway)
    credentials = get_credentials_from_args(args, base)
    
    manager = PublisherManager(project_root)
    info = manager.get_info(credentials)
    
    show_publish_info(base, info)
    
    # Also run the actual info display from publishers
    if credentials:
        try:
            from publishing.npm_publisher import NpmPublisher
            from publishing.maven_publisher import MavenPublisher
            
            base.print_status("NPM Packages Information:")
            npm_publisher = NpmPublisher(
                project_root=project_root,
                enabled_publications=None,  # None = show info
                credentials=credentials
            )
            npm_publisher.run()
            
            print()  # Add spacing
            
            base.print_status("Maven Artifacts Information:")
            maven_publisher = MavenPublisher(
                project_root=project_root,
                enabled_publications=None,  # None = show info
                credentials=credentials
            )
            maven_publisher.run()
        except Exception as e:
            base.print_warning(f"Could not display detailed info: {e}")
    
    return 0

