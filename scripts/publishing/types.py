"""
Publishing Types
Data classes for structured results from publishing operations
"""

from dataclasses import dataclass
from typing import Optional, List, Dict, Any
from pathlib import Path


@dataclass
class PublishResult:
    """Result of a publish operation"""
    success: bool
    target: str  # 'npm', 'maven', or 'all'
    error_message: Optional[str] = None
    details: Optional[Dict[str, Any]] = None


@dataclass
class StatusInfo:
    """Information about project status"""
    version: Optional[str] = None
    version_file_exists: bool = False
    js_build_exists: bool = False
    android_build_exists: bool = False
    gradle_available: bool = False
    gradle_version: Optional[str] = None
    gradlew_path: Optional[str] = None
    npm_available: bool = False
    npm_version: Optional[str] = None
    github_user: Optional[str] = None
    github_token_set: bool = False


@dataclass
class PublishInfo:
    """Detailed information about packages and artifacts"""
    npm_packages: List[Dict[str, str]] = None
    maven_artifacts: List[Dict[str, str]] = None
    error_message: Optional[str] = None

