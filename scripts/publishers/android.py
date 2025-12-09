"""
Android Publisher Module
Provides Android artifact publishing functionality
"""

from typing import Optional, List, Dict
from .base_builder import BaseBuilder, GitHubCredentials


class AndroidPublisher(BaseBuilder):
    """Main class for Android artifact publishing functionality"""
    
    def __init__(
        self, 
        enabled_publications: Optional[List[str]] = None,
        credentials: Optional[GitHubCredentials] = None
    ):
        super().__init__(
            "Zernikalos Android Publisher", 
            enabled_publications=enabled_publications,
            credentials=credentials
        )
    
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


def run_android_publish(github_user: str, github_token: str, action: str = "all_publications") -> bool:
    """
    Run Android publish functionality programmatically
    
    Args:
        github_user: GitHub username/organization
        github_token: GitHub access token
        action: Action to perform (debug, release, all, all_publications, info)
        
    Returns:
        True if successful, False otherwise
    """
    from .base_builder import GitHubCredentials
    
    # Map action to enabled publications
    action_to_publications = {
        "debug": ["debug"],
        "release": ["release"],
        "all": ["all"],
        "all_publications": ["all_publications"],
        "info": None
    }
    
    enabled_publications = action_to_publications.get(action)
    
    # Create credentials object
    credentials = GitHubCredentials(user=github_user, token=github_token)
    
    # Create publisher with credentials
    publisher = AndroidPublisher(
        enabled_publications=enabled_publications,
        credentials=credentials
    )
    
    return publisher.run() == 0

