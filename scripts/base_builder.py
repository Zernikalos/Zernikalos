#!/usr/bin/env python3
"""
Base Builder
Abstract base class for all Zernikalos publishers
"""

from abc import ABC, abstractmethod
from pathlib import Path
from typing import Optional, Any, List, Dict
from common import BaseScript


class BaseBuilder(BaseScript, ABC):
    """Abstract base class for all Zernikalos publishers"""
    
    def __init__(self, script_name: str = "Zernikalos Builder", enabled_publications: Optional[List[str]] = None):
        super().__init__(script_name)
        self.enabled_publications = enabled_publications or []
    
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
        if not self.enabled_publications:
            self.print_warning("No publications enabled. Nothing to publish.")
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
    
    def _handle_action(self, args: Any) -> int:
        """
        Handle specific action based on command line arguments
        
        Base implementation handles common patterns (info/list flags).
        Can be overridden for custom behavior.
        
        Args:
            args: Parsed command line arguments
            
        Returns:
            Exit code (0 for success, 1 for failure)
        """
        # Check for info flag (common pattern)
        if hasattr(args, 'info') and args.info:
            return 0 if self.get_publish_info() else 1
        
        # Check for list flag (common pattern, e.g., NPM)
        if hasattr(args, 'list') and args.list:
            return 0 if self.get_publish_info() else 1
        
        # Otherwise, publish enabled publications
        return 0 if self.publish() else 1
    
    def run(self, args) -> int:
        """
        Main execution method - common flow for all builders
        
        Args:
            args: Parsed command line arguments
            
        Returns:
            Exit code (0 for success, 1 for failure)
        """
        # Set credentials from command line if provided
        self.set_credentials_from_args(args)
        
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
        
        # Get project version (if needed by the builder)
        # Some builders may need version, others may not
        # Only check version if info is requested or if builder needs it
        if hasattr(args, 'info') and args.info:
            version = self.get_project_version()
            if version is None:
                self.print_warning("Could not get project version, but continuing...")
        
        # Handle specific action (info or publish)
        return self._handle_action(args)

