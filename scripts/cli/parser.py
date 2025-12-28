"""
CLI Parser
Main argument parser with subcommands for Zernikalos management
"""

import argparse
from .common_args import add_common_arguments


def create_parser() -> argparse.ArgumentParser:
    """
    Create the main argument parser with all subcommands
    
    Returns:
        Configured ArgumentParser instance
    """
    parser = argparse.ArgumentParser(
        prog='zmanager',
        description='Zernikalos Management Tool - Unified script for versioning and publishing',
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  # Versioning
  python3 zmanager.py version 0.6.0
  python3 zmanager.py version 0.6.0 --no-push
  python3 zmanager.py version --show-next
  
  # Publishing
  python3 zmanager.py publish --all
  python3 zmanager.py publish --npm
  python3 zmanager.py publish --maven
  
  # Release (version + optional publish)
  python3 zmanager.py release 0.6.0
  python3 zmanager.py release 0.6.0 --no-publish
  python3 zmanager.py release --auto
  
  # Info/Status
  python3 zmanager.py status
  python3 zmanager.py info
        """
    )
    
    # Add common arguments
    add_common_arguments(parser)
    
    # Create subparsers
    subparsers = parser.add_subparsers(dest='command', help='Available commands')
    
    # Import argument functions from handlers
    from .version_cli import add_version_arguments, add_release_arguments
    from .publisher_cli import add_publish_arguments, add_status_arguments, add_info_arguments
    
    # Version subcommand
    version_parser = subparsers.add_parser('version', help='Version management commands')
    add_version_arguments(version_parser)
    
    # Publish subcommand
    publish_parser = subparsers.add_parser('publish', help='Publishing commands')
    add_publish_arguments(publish_parser)
    
    # Release subcommand
    release_parser = subparsers.add_parser('release', help='Complete release (version + optional publish)')
    add_release_arguments(release_parser)
    
    # Status subcommand
    status_parser = subparsers.add_parser('status', help='Show project status and available artifacts')
    add_status_arguments(status_parser)
    
    # Info subcommand
    info_parser = subparsers.add_parser('info', help='Show detailed information about packages and artifacts')
    add_info_arguments(info_parser)
    
    return parser

