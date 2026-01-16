"""
Versioning Types
Data classes for structured results from version management operations
"""

from dataclasses import dataclass
from typing import Optional, List
from tools import VersionBump


@dataclass
class ValidationResult:
    """Result of version validation"""
    is_valid: bool
    error_message: Optional[str] = None


@dataclass
class ReleaseResult:
    """Result of release execution"""
    success: bool
    version: str
    steps_completed: List[str]
    error_message: Optional[str] = None


@dataclass
class PushResult:
    """Result of push operation"""
    success: bool
    version: str
    error_message: Optional[str] = None


@dataclass
class VersionInfo:
    """Information about calculated version"""
    base_version: str
    next_version: str
    maven_version: str
    npm_version: str
    commits: List[str]
    bump_type: VersionBump
    last_tag: Optional[str] = None


@dataclass
class ReleaseConfig:
    """Configuration for release operation"""
    version: str
    no_push: bool = False
    dry_run: bool = False

