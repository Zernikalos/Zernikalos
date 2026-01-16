"""
Common CLI Arguments
Shared argument definitions for Zernikalos CLI commands
"""

import argparse


def add_common_arguments(parser: argparse.ArgumentParser) -> None:
    """
    Add common command line arguments to a parser
    
    Args:
        parser: ArgumentParser instance to add arguments to
    """
    parser.add_argument('-u', '--user', 
                       help='GitHub organization/user')
    parser.add_argument('-t', '--token',
                       help='GitHub access token')

