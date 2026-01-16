#!/usr/bin/env python3
"""
Zernikalos Manager
Unified script for versioning and publishing Zernikalos artifacts
"""

import sys
from pathlib import Path
from cli import create_parser
from cli.version_cli import handle_version_command, handle_release_command
from cli.publisher_cli import handle_publish_command, handle_status_command, handle_info_command
from common import BaseScript



def main():
    """Main entry point"""
    parser = create_parser()
    args = parser.parse_args()
    
    # Check if command was provided
    if not args.command:
        parser.print_help()
        return 1
    
    # Initialize base script for common functionality
    base = BaseScript("Zernikalos Manager")
    
    # Check directory first
    if not base.check_directory():
        return 1
    
    # Route to appropriate handler
    if args.command == 'version':
        return handle_version_command(args, base.project_root)
    
    elif args.command == 'publish':
        return handle_publish_command(args, base.project_root)
    
    elif args.command == 'release':
        return handle_release_command(args, base.project_root)
    
    elif args.command == 'status':
        return handle_status_command(base.project_root)
    
    elif args.command == 'info':
        return handle_info_command(args, base.project_root)
    
    else:
        parser.print_help()
        return 1


if __name__ == "__main__":
    sys.exit(main())

