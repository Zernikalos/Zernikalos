#!/usr/bin/env python3
"""
Base Builder
Abstract base class for all Zernikalos publishers
"""

from abc import ABC, abstractmethod
from pathlib import Path
from typing import Optional, Any
from common import BaseScript


class BaseBuilder(BaseScript, ABC):
    """Abstract base class for all Zernikalos publishers"""
    
    def __init__(self, script_name: str = "Zernikalos Builder"):
        super().__init__(script_name)
    
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
    def publish(self, *args, **kwargs) -> bool:
        """
        Publish artifacts to the repository
        
        Args:
            *args: Variable positional arguments
            **kwargs: Variable keyword arguments
            
        Returns:
            True if publish is successful, False otherwise
        """
        pass
    
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
    
    @abstractmethod
    def _get_default_action(self) -> int:
        """
        Get the default action when no specific action is provided
        
        Returns:
            Exit code (0 for success, 1 for failure)
        """
        pass
    
    @abstractmethod
    def _handle_action(self, args: Any) -> int:
        """
        Handle specific action based on command line arguments
        
        Args:
            args: Parsed command line arguments
            
        Returns:
            Exit code (0 for success, 1 for failure)
        """
        pass
    
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
        
        # Handle specific action
        return self._handle_action(args)

