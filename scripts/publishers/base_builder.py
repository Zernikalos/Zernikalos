#!/usr/bin/env python3
"""
Base Builder
Abstract base class for all Zernikalos publishers
"""

from abc import ABC, abstractmethod
from pathlib import Path
from typing import Optional, Any, List, Dict
from dataclasses import dataclass
from common import BaseScript


@dataclass
class GitHubCredentials:
    """GitHub credentials container"""
    user: str
    token: str
    
    def is_valid(self) -> bool:
        """Check if credentials are valid"""
        return bool(self.user and self.token)


class BaseBuilder(BaseScript, ABC):
    """Abstract base class for all Zernikalos publishers"""
    
    def __init__(
        self, 
        script_name: str = "Zernikalos Builder", 
        enabled_publications: Optional[List[str]] = None,
        credentials: Optional[GitHubCredentials] = None
    ):
        super().__init__(script_name)
        # Keep None to distinguish between "show info" (None) and "publish nothing" ([])
        self.enabled_publications = enabled_publications
        self.credentials = credentials
        # Sync credentials with parent class attributes for compatibility
        if credentials:
            self._sync_credentials_to_attributes()
    
    @abstractmethod
    def authentication(self) -> bool:
        """
        Setup and verify authentication
        
        Returns:
            True if authentication is configured successfully, False otherwise
        """
        pass
    
    @abstractmethod
    def build(self) -> bool:
        """
        Build the project artifacts
        
        Returns:
            True if build is successful, False otherwise
        """
        pass
    
    @abstractmethod
    def build_verify(self) -> bool:
        """
        Verify that build directory exists and artifacts are ready
        
        Returns:
            True if build is verified, False otherwise (may trigger auto-build)
        """
        pass
    
    @abstractmethod
    def get_available_publications(self) -> List[Dict[str, str]]:
        """
        Get list of available publications
        
        Returns:
            List of dictionaries with keys: id, name, description
        """
        pass
    
    def _validate_publications(self) -> bool:
        """
        Validate that enabled publications exist
        
        Returns:
            True if valid, False if invalid or empty
        """
        # Should not be called if enabled_publications is None or empty
        if not self.enabled_publications:
            return False
        
        available = {pub["id"] for pub in self.get_available_publications()}
        invalid = [pub_id for pub_id in self.enabled_publications if pub_id not in available]
        
        if invalid:
            self.print_error(f"Invalid publication IDs: {invalid}")
            return False
        
        return True
    
    def _get_publication_name(self, pub_id: str) -> str:
        """
        Get human-readable name for a publication ID
        
        Args:
            pub_id: Publication ID
            
        Returns:
            Publication name or ID if not found
        """
        publications = self.get_available_publications()
        for pub in publications:
            if pub["id"] == pub_id:
                return pub["name"]
        return pub_id
    
    def _print_publishing_start(self, pub_id: str) -> None:
        """Print message when starting to publish a publication"""
        pub_name = self._get_publication_name(pub_id)
        self.print_status(f"Publishing {pub_name}...")
    
    def _print_publishing_success(self, pub_id: str) -> None:
        """Print success message after publishing a publication"""
        pub_name = self._get_publication_name(pub_id)
        self.print_success(f"{pub_name} published successfully!")
    
    def _print_publishing_error(self, pub_id: str, error_msg: str = None) -> None:
        """Print error message when publishing fails"""
        pub_name = self._get_publication_name(pub_id)
        if error_msg:
            self.print_error(f"Failed to publish {pub_name}: {error_msg}")
        else:
            self.print_error(f"Failed to publish {pub_name}")
    
    def _print_unknown_publication(self, pub_id: str) -> None:
        """Print error for unknown publication ID"""
        self.print_error(f"Unknown publication ID: {pub_id}")
    
    def _sync_credentials_to_attributes(self) -> None:
        """Sync credentials object to instance attributes"""
        if self.credentials:
            self.github_user = self.credentials.user
            self.github_token = self.credentials.token
    
    def _ensure_credentials(self, args: Optional[Any] = None) -> bool:
        """
        Ensure credentials are available, trying multiple sources
        
        Returns:
            True if credentials are available, False otherwise
        """
        # First, try to get from args if provided
        if args:
            self.set_credentials_from_args(args)
            # Create credentials object if args provided them
            if self.github_user and self.github_token:
                self.credentials = GitHubCredentials(user=self.github_user, token=self.github_token)
        
        # If we still don't have credentials, try environment/prompt
        if not self.credentials:
            if not self.get_github_credentials():
                return False
            # Create credentials object from instance attributes
            if self.github_user and self.github_token:
                self.credentials = GitHubCredentials(user=self.github_user, token=self.github_token)
        
        # Sync credentials object to instance attributes
        self._sync_credentials_to_attributes()
        return True
    
    def _print_build_start(self, build_type: str = "project") -> None:
        """Print message when starting build"""
        self.print_status(f"Building {build_type}...")
    
    def _print_build_auto(self, build_dir: str) -> None:
        """Print messages when auto-building"""
        self.print_warning(f"Build directory '{build_dir}' does not exist")
        self.print_status("Building automatically...")
    
    @abstractmethod
    def _publish_publication(self, pub_id: str) -> bool:
        """
        Publish a specific publication by ID
        
        Args:
            pub_id: Publication ID to publish
            
        Returns:
            True if publish is successful, False otherwise
        """
        pass
    
    def publish(self) -> bool:
        """
        Publish artifacts to the repository
        
        Publishes only the publications specified in enabled_publications.
        Base implementation handles validation and iteration.
        
        Returns:
            True if publish is successful, False otherwise
        """
        if not self._validate_publications():
            return True  # No publications or invalid, but not an error
        
        success = True
        
        # Publish each enabled publication
        for pub_id in self.enabled_publications:
            self._print_publishing_start(pub_id)
            
            if not self._publish_publication(pub_id):
                self._print_publishing_error(pub_id)
                success = False
            else:
                self._print_publishing_success(pub_id)
            
            # Add spacing between publications
            if pub_id != self.enabled_publications[-1]:
                print()
        
        return success
    
    @abstractmethod
    def get_publish_info(self) -> bool:
        """
        Get and display publication information
        
        Returns:
            True if info was displayed successfully, False otherwise
        """
        pass
    
    @abstractmethod
    def _check_tool(self) -> bool:
        """
        Check if the required tool (npm/gradle) is available
        
        Returns:
            True if tool is available, False otherwise
        """
        pass
    
    def run(self, args: Optional[Any] = None) -> int:
        """
        Main execution method - common flow for all builders
        
        Args:
            args: Optional parsed command line arguments (for backward compatibility)
            
        Returns:
            Exit code (0 for success, 1 for failure)
        """
        # Ensure credentials are available
        if not self._ensure_credentials(args):
            return 1
        
        # Check prerequisites
        if not self.check_directory():
            return 1
        
        # Check required tool (npm/gradle)
        if not self._check_tool():
            return 1
        
        # Setup authentication
        if not self.authentication():
            return 1
        
        # Verify build (may trigger auto-build)
        if not self.build_verify():
            return 1
        
        # Determine action based on enabled_publications
        # None = show info, [] = no action, [items] = publish
        if self.enabled_publications is None:
            return 0 if self.get_publish_info() else 1
        elif not self.enabled_publications:
            return 0
        else:
            return 0 if self.publish() else 1

