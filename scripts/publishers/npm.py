"""
NPM Publisher Module
Provides NPM package publishing functionality
"""

from typing import Optional, List, Tuple, Any, Dict
from .base_builder import BaseBuilder, GitHubCredentials


class NpmPublisher(BaseBuilder):
    """Main class for NPM package publishing functionality"""

    def __init__(
        self, 
        enabled_publications: Optional[List[str]] = None,
        credentials: Optional[GitHubCredentials] = None
    ):
        super().__init__(
            "Zernikalos NPM Publisher", 
            enabled_publications=enabled_publications,
            credentials=credentials
        )
    
    def get_available_publications(self) -> List[Dict[str, str]]:
        """Get list of available publications"""
        return [
            {"id": "all", "name": "All Packages", "description": "Publish main zernikalos package"}
        ]
    
    def authentication(self) -> bool:
        """Setup and verify authentication"""
        if not self.github_token:
            self.print_warning("GitHub token not found")
            self.print_status("Setting up npm authentication...")
            if not self.get_github_credentials():
                return False
        
        # Set npm authentication token
        self.npm.set_auth_token(self.github_token)
        return True
    
    def build(self) -> bool:
        """Build packages with webpack"""
        return self.gradle.js_browser_production_webpack()
    
    def build_verify(self) -> bool:
        """Verify that build directory exists and artifacts are ready"""
        build_dir = self.project_root / "build" / "js"
        if not build_dir.exists():
            self._print_build_auto("build/js")
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
        if pub_id == "all":
            return self._publish_workspace(None)
        else:
            self._print_unknown_publication(pub_id)
            return False
    
    def get_publish_info(self) -> bool:
        """Get and display publication information"""
        packages = self._list_packages()
        if not packages:
            return False
        
        print()
        self.print_status("To publish packages, use: python publish-all.py -n")
        return True
    
    def _check_tool(self) -> bool:
        """Check if npm is available"""
        return self.check_npm()
    
    # Private methods for internal operations
    def _list_packages(self, exclude_test: bool = True) -> List[Tuple[str, str]]:
        """List available packages, optionally excluding test packages"""
        self.print_status("Available packages in build/js/packages/@zernikalos/:")

        packages = self.npm.list_packages(exclude_test=exclude_test)
        
        if not packages:
            self.print_error("No packages found in build/js/packages/@zernikalos/")
        else:
            for package_name, version in packages:
                print(f"  - {package_name} (v{version})")

        return packages
    
    def _publish_workspace(self, package_filter: Optional[str] = None) -> bool:
        """Publish packages using npm workspace with optional filter"""
        workspace_dir = self.project_root / "build" / "js"

        if not workspace_dir.exists():
            self.print_error("Workspace directory 'build/js' not found")
            return False

        # Get workspace packages info
        packages = self._list_packages()
        if not packages:
            return False

        # Filter packages if specified
        if package_filter:
            # Additional safety: exclude test packages even when filter is specified
            filtered_packages = [(name, version) for name, version in packages 
                               if package_filter in name and 'test' not in name.lower()]
            if not filtered_packages:
                self.print_error(f"No packages found matching filter: {package_filter}")
                return False
            packages = filtered_packages
        else:
            # When no filter specified, only publish the main zernikalos package
            # Test packages are already excluded by list_packages()
            main_package = [(name, version) for name, version in packages if name == "zernikalos"]
            if not main_package:
                self.print_error("Main zernikalos package not found")
                return False
            packages = main_package

        # Show packages to be published
        self.print_status("Packages to be published:")
        for package_name, version in packages:
            print(f"  - @zernikalos/{package_name} (v{version})")

        # Ensure npm has the auth token
        self.npm.set_auth_token(self.github_token)

        # Publish using npm tool
        success, stdout, stderr = self.npm.publish_workspace(
            workspace_dir,
            package_filter=package_filter,
            show_output=True
        )

        if success:
            for package_name, version in packages:
                self.print_status(f"  - @zernikalos/{package_name} v{version}")
                self.print_status(f"    Available at: https://npm.pkg.github.com/@zernikalos/{package_name}")
        
        return success


def run_npm_publish(github_user: str, github_token: str, action: str = "all") -> bool:
    """
    Run NPM publish functionality programmatically
    
    Args:
        github_user: GitHub username/organization
        github_token: GitHub access token
        action: Action to perform (list, all)
        
    Returns:
        True if successful, False otherwise
    """
    from .base_builder import GitHubCredentials
    
    # Map action to enabled publications
    action_to_publications = {
        "list": None,
        "all": ["all"]
    }
    
    enabled_publications = action_to_publications.get(action)
    
    # Create credentials object
    credentials = GitHubCredentials(user=github_user, token=github_token)
    
    # Create publisher with credentials
    publisher = NpmPublisher(
        enabled_publications=enabled_publications,
        credentials=credentials
    )
    
    return publisher.run() == 0

