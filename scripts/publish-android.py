#!/usr/bin/env python3
"""
Zernikalos Android Publish Script
Auxiliary script for publishing Android artifacts to GitHub Packages Maven Repository

This script is designed to be called from publish-all.py, but can also be run independently.
"""

import sys
import argparse
from pathlib import Path
from typing import Optional, List, Tuple, Any, Dict
from common import add_common_arguments
from base_builder import BaseBuilder


class AndroidPublisher(BaseBuilder):
    """Main class for Android artifact publishing functionality"""
    
    def __init__(self, enabled_publications: Optional[List[str]] = None):
        super().__init__("Zernikalos Android Publisher", enabled_publications=enabled_publications)
    
    def get_available_publications(self) -> List[Dict[str, str]]:
        """Get list of available publications"""
        return [
            {"id": "debug", "name": "Debug", "description": "Android Debug artifacts"},
            {"id": "release", "name": "Release", "description": "Android Release artifacts"},
            {"id": "all", "name": "All Android", "description": "Both Debug and Release artifacts"},
            {"id": "all_publications", "name": "All Publications", "description": "All Maven publications (Android + JS)"}
        ]
    
    def authentication(self) -> bool:
        """Setup and verify authentication"""
        return self.get_github_credentials()
    
    def build(self) -> bool:
        """Build the Android project"""
        return self.gradle.build()
    
    def build_verify(self) -> bool:
        """Verify that build directory exists and artifacts are ready"""
        build_dir = self.project_root / "build"
        if not build_dir.exists():
            self._print_build_auto("build")
            return self.build()
        return True
    
    def _publish_publication(self, pub_id: str) -> bool:
        """
        Publish a specific publication by ID
        
        Args:
            pub_id: Publication ID to publish
            
        Returns:
            True if publish is successful, False otherwise
        """
        if pub_id == "debug":
            return self._publish_debug()
        elif pub_id == "release":
            return self._publish_release()
        elif pub_id == "all":
            return self._publish_all_android()
        elif pub_id == "all_publications":
            return self._publish_all_publications()
        else:
            self._print_unknown_publication(pub_id)
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
    
    # _handle_action is inherited from BaseBuilder and works for AndroidPublisher
    
    # Private methods for internal publishing operations
    def _publish_debug(self) -> bool:
        """Publish Android Debug artifacts to GitHub Packages"""
        return self.gradle.publish_android_debug(self.github_user, self.github_token)
    
    def _publish_release(self) -> bool:
        """Publish Android Release artifacts to GitHub Packages"""
        return self.gradle.publish_android_release(self.github_user, self.github_token)
    
    def _publish_all_android(self) -> bool:
        """Publish both Android Debug and Release artifacts"""
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
        self.print_status("This will publish:")
        self.print_status("  - dev.zernikalos:zernikalos (Android)")
        self.print_status("  - dev.zernikalos:zernikalos-js (JavaScript/Kotlin)")
        self.print_status("  - Any other configured publications")
        
        return self.gradle.publish_all_publications(self.github_user, self.github_token)
        
        

def _select_publications_interactive(publisher: AndroidPublisher) -> Optional[List[str]]:
    """Interactive selection of publications to enable"""
    publications = publisher.get_available_publications()
    
    publisher.get_publish_info()
    print()
    publisher.print_status("Available publications:")
    for i, pub in enumerate(publications, 1):
        print(f"  {i}. {pub['name']} ({pub['id']}) - {pub['description']}")
    
    print()
    try:
        response = input("Select publications to publish (comma-separated numbers, or 'all' for all): ").strip().lower()
        
        if response == 'all':
            return [pub['id'] for pub in publications]
        
        # Parse comma-separated numbers
        selected_indices = [int(x.strip()) - 1 for x in response.split(',') if x.strip().isdigit()]
        
        if not selected_indices:
            publisher.print_warning("No valid selections. Nothing will be published.")
            return []
        
        # Validate indices
        valid_indices = [i for i in selected_indices if 0 <= i < len(publications)]
        if len(valid_indices) != len(selected_indices):
            publisher.print_warning("Some selections were invalid. Using valid selections only.")
        
        return [publications[i]['id'] for i in valid_indices]
        
    except (ValueError, KeyboardInterrupt):
        print()
        publisher.print_warning("Operation cancelled by user")
        return None


def main():
    """
    Main entry point for standalone execution
    
    Note: This script is primarily designed to be called from publish-all.py.
    This function provides backward compatibility for direct execution.
    """
    parser = argparse.ArgumentParser(
        description="Zernikalos Android Publish Script (auxiliary)",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  python publish-android.py                                    # Interactive mode
  python publish-android.py -i                                 # Show publication info
  python publish-android.py -d                                 # Publish Debug artifacts
  python publish-android.py -r                                 # Publish Release artifacts
  python publish-android.py -a                                 # Publish all Android artifacts (Debug + Release)
  python publish-android.py --all-publications                 # Publish ALL publications (recommended)

Note: For complete publishing workflow, use publish-all.py instead.
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
    
    # Determine enabled publications from args
    enabled_publications: Optional[List[str]] = None
    
    if args.info:
        enabled_publications = None
    elif args.debug:
        enabled_publications = ["debug"]
    elif args.release:
        enabled_publications = ["release"]
    elif args.all:
        enabled_publications = ["all"]
    elif args.all_publications:
        enabled_publications = ["all_publications"]
    else:
        # No specific action, create temporary publisher for interactive selection
        temp_publisher = AndroidPublisher()
        enabled_publications = _select_publications_interactive(temp_publisher)
        if enabled_publications is None:
            return 0  # User cancelled
    
    # Create publisher with enabled publications and run
    publisher = AndroidPublisher(enabled_publications=enabled_publications)
    return publisher.run(args)


def run_android_publish(github_user: str, github_token: str, action: str = "all_publications") -> bool:
    """
    Run Android publish functionality programmatically
    
    This is the main entry point called by publish-all.py
    
    Args:
        github_user: GitHub username/organization
        github_token: GitHub access token
        action: Action to perform (debug, release, all, all_publications, info)
        
    Returns:
        True if successful, False otherwise
    """
    # Map action to enabled publications
    action_to_publications = {
        "debug": ["debug"],
        "release": ["release"],
        "all": ["all"],
        "all_publications": ["all_publications"],
        "info": None
    }
    
    enabled_publications = action_to_publications.get(action)
    
    # Create a mock args object
    class MockArgs:
        def __init__(self, info=False, user="", token=""):
            self.info = info
            self.user = user
            self.token = token
    
    args = MockArgs(
        info=(action == "info"),
        user=github_user,
        token=github_token
    )
    
    publisher = AndroidPublisher(enabled_publications=enabled_publications)
    publisher.github_user = github_user
    publisher.github_token = github_token
    
    return publisher.run(args) == 0


if __name__ == '__main__':
    sys.exit(main())
