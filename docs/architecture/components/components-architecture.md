# Components Architecture

This document provides a comprehensive overview of the component system architecture in the Zernikalos Engine. The component system is designed around a capability-based architecture that promotes code reusability, separation of concerns, and flexible composition.

## Overview

The Zernikalos component system provides a clean, type-safe foundation for building complex game objects and systems. Components can have different capabilities:

- **Reference Management**: Unique identification and referencing
- **Initialization**: Context-aware component setup  
- **Rendering**: Visual representation and rendering logic
- **Serialization**: Data persistence and restoration

## Public API

### Core Interfaces

#### ZRef

The foundational interface for all reference-based entities in the engine.

```kotlin
interface ZRef {
    val refId: String
}
```

**Purpose**: Provides unique identification for components that can be referenced by other systems.

**Key Features**:
- Generates unique UUIDs for component identification
- Enables efficient retrieval and management in reference-based systems
- Required by all components in the engine

#### ZComponent

The base interface for all components that can be initialized and rendered.

```kotlin
interface ZComponent: ZRef {
    val isInitialized: Boolean
    val isRenderizable: Boolean
    fun initialize(ctx: ZRenderingContext)
}
```

**Purpose**: Defines the contract for components that interact with the rendering context.

**Key Features**:
- Inherits reference capabilities from `ZRef`
- Tracks initialization state
- Indicates rendering capability
- Provides initialization method with rendering context

### Abstract Base Classes

#### ZRenderizableComponent

Abstract class for components that can be rendered.

```kotlin
abstract class ZRenderizableComponent<R: ZComponentRenderer>(): ZBaseComponent()
```

**Purpose**: Provides rendering capabilities to components that need visual representation.

**Key Features**:
- Automatically flags component as renderizable
- Provides type-safe renderer access
- Handles renderer creation during initialization

**Usage Pattern**:
```kotlin
class MyRenderableComponent : ZRenderizableComponent<MyRenderer>() {
    override fun createRenderer(ctx: ZRenderingContext): MyRenderer {
        return MyRenderer(ctx)
    }
}
```

#### ZSerializableComponent

Abstract class for components that can be serialized.

```kotlin
abstract class ZSerializableComponent<D: ZComponentData>(data: D): ZBaseComponent()
```

**Purpose**: Provides serialization capabilities to components that need data persistence.

**Key Features**:
- Automatically flags component as serializable
- Synchronizes UUID between component and data
- Provides type-safe data access

**Usage Pattern**:
```kotlin
class MySerializableComponent(data: MyData) : ZSerializableComponent<MyData>(data) {
    // Component implementation
}
```

#### ZOmniComponent

Abstract class for components that combine both rendering and serialization capabilities.

```kotlin
abstract class ZOmniComponent<D: ZComponentData, R: ZComponentRenderer>(data: D): ZBaseComponent()
```

**Purpose**: Provides both rendering and serialization capabilities in a single component.

**Key Features**:
- Automatically flags component as both renderizable and serializable
- Ideal for complex objects that need visual state persistence
- Handles both renderer creation and data management

**Usage Pattern**:
```kotlin
class MyOmniComponent(data: MyData) : ZOmniComponent<MyData, MyRenderer>(data) {
    override fun createRenderer(ctx: ZRenderingContext): MyRenderer {
        return MyRenderer(ctx)
    }
}
```

### Data and Renderer Classes

#### ZComponentData

Abstract base class for component data objects.

```kotlin
abstract class ZComponentData: ZLoggable, ZRef
```

**Purpose**: Provides a foundation for serializable data associated with components.

**Key Features**:
- UUID generation and management
- Reference ID calculation
- Abstract `toString()` method for custom string representation
- Transient `refId` property for serialization

#### ZComponentRenderer

Abstract base class for component renderers.

```kotlin
abstract class ZComponentRenderer internal constructor(protected val ctx: ZRenderingContext): ZLoggable
```

**Purpose**: Provides a foundation for implementing component-specific rendering logic.

**Key Features**:
- Internal constructor for controlled instantiation
- Access to rendering context
- Lifecycle methods: `initialize()`, `bind()`, `unbind()`, `render()`
- Logging capabilities

### Serialization System

#### ZComponentSerializer

Abstract base class for serializing component instances.

```kotlin
abstract class ZComponentSerializer<T: ZComponent, D: Any>: KSerializer<T>
```

**Purpose**: Provides a framework for converting components to and from serialized data formats.

**Key Features**:
- Type-safe serialization and deserialization
- Support for both `ZSerializableComponent` and `ZOmniComponent`
- Abstract factory method for component creation
- Serialization capability validation

**Usage Pattern**:
```kotlin
class MyComponentSerializer : ZComponentSerializer<MyComponent, MyData>() {
    override val kSerializer: KSerializer<MyData> = MyData.serializer()
    
    override fun createComponentInstance(data: MyData): MyComponent {
        return MyComponent(data)
    }
}
```

### Supporting Interfaces

#### ZBindeable

Interface for resources that require binding/unbinding operations.

```kotlin
interface ZBindeable {
    fun bind()
    fun unbind()
}
```

**Purpose**: Defines the contract for resources that need preparation and cleanup.

#### ZRenderizable

Interface for objects that can be rendered.

```kotlin
interface ZRenderizable {
    fun render()
}
```

**Purpose**: Defines the contract for objects with visual representation.

#### ZResizable

Interface for objects that respond to viewport changes.

```kotlin
interface ZResizable {
    fun onViewportResize(width: Int, height: Int)
}
```

**Purpose**: Defines the contract for objects that need to respond to viewport dimension changes.

## Component Hierarchy

```
ZRef (interface)
└── ZComponent (interface)
    └── ZBaseComponent (abstract)
        ├── ZRenderizableComponent<R> (abstract)
        ├── ZSerializableComponent<D> (abstract)
        └── ZOmniComponent<D, R> (abstract)
```

## Best Practices

### Component Creation

1. **Choose the Right Base Class**:
   - Use `ZRenderizableComponent` for visual-only components
   - Use `ZSerializableComponent` for data-only components
   - Use `ZOmniComponent` for components needing both capabilities

2. **Initialization**:
   - Always call `super.initialize(ctx)` in overridden methods
   - Implement `createRenderer()` for renderable components
   - Provide data objects for serializable components

3. **UUID Management**:
   - UUIDs are automatically generated and managed
   - Data and component UUIDs are synchronized automatically
   - Use `refId` for referencing components

### Serialization

1. **Data Design**:
   - Extend `ZComponentData` for serializable data
   - Implement meaningful `toString()` methods
   - Use `@Transient` for non-serializable properties

2. **Serializer Implementation**:
   - Extend `ZComponentSerializer` for custom serialization
   - Provide proper `kSerializer` for data types
   - Implement `createComponentInstance()` for deserialization

### Rendering

1. **Renderer Design**:
   - Extend `ZComponentRenderer` for custom renderers
   - Implement lifecycle methods as needed
   - Use the provided rendering context

2. **Resource Management**:
   - Implement `bind()` and `unbind()` for resource management
   - Handle viewport changes through `ZResizable` if needed

## Common Patterns

### Simple Renderable Component
```kotlin
class SimpleMesh(data: MeshData) : ZRenderizableComponent<MeshRenderer>(data) {
    override fun createRenderer(ctx: ZRenderingContext): MeshRenderer {
        return MeshRenderer(ctx, data)
    }
}
```

### Serializable Data Component
```kotlin
class GameSettings(data: SettingsData) : ZSerializableComponent<SettingsData>(data) {
    // Settings management logic
}
```

### Full-Featured Component
```kotlin
class GameObject(data: GameObjectData) : ZOmniComponent<GameObjectData, GameObjectRenderer>(data) {
    override fun createRenderer(ctx: ZRenderingContext): GameObjectRenderer {
        return GameObjectRenderer(ctx, data)
    }
}
```

---

## Internal Architecture

This section covers the internal implementation details of the component system. These classes and interfaces are used internally by the public API and are not typically used directly by developers.

### Mixin Interfaces

#### ZRenderizableMixin

A mixin interface for components that can be rendered.

```kotlin
interface ZRenderizableMixin<R: ZComponentRenderer> {
    val isRenderizable: Boolean
    val renderer: R
    fun createRenderer(ctx: ZRenderingContext): R
}
```

**Purpose**: Provides rendering capabilities to components through composition.

**Key Features**:
- Type-safe renderer access
- Lazy renderer creation
- Separation of rendering concerns

#### ZSerializableMixin

A mixin interface for components that can be serialized.

```kotlin
interface ZSerializableMixin<D: ZComponentData> {
    val data: D
}
```

**Purpose**: Provides serialization capabilities to components.

**Key Features**:
- Access to serializable data
- Type-safe data management
- Separation of serialization concerns

### Implementation Classes

#### ZBaseComponent

The foundational abstract class for all components.

```kotlin
abstract class ZBaseComponent(): ZComponent, ZLoggable
```

**Purpose**: Provides common functionality and state management for all components.

**Key Features**:
- UUID generation and management
- Initialization state tracking
- Capability flag management (`isRenderizable`, `isSerializable`)
- Protected methods for capability setup
- Single initialization enforcement

**Internal State**:
- `uuid`: Unique identifier (lazy-generated)
- `initialized`: Initialization state flag
- `_isRenderizable`: Rendering capability flag
- `_isSerializable`: Serialization capability flag

#### ZRenderizableImpl

Implementation of the `ZRenderizableMixin` interface.

```kotlin
class ZRenderizableImpl<R: ZComponentRenderer>(
    private val component: ZBaseComponent,
    private val rendererFactory: (ZRenderingContext) -> R
): ZRenderizableMixin<R>
```

**Purpose**: Encapsulates rendering logic and provides clean separation of concerns.

**Key Features**:
- Lazy renderer creation
- Initialization validation
- Factory-based renderer instantiation
- Error handling for uninitialized components

#### ZSerializableImpl

Implementation of the `ZSerializableMixin` interface.

```kotlin
class ZSerializableImpl<D: ZComponentData>(
    private val component: ZBaseComponent,
    override val data: D
): ZSerializableMixin<D>
```

**Purpose**: Encapsulates serialization logic and manages component-data synchronization.

**Key Features**:
- UUID synchronization between component and data
- Type-safe data access
- Clean separation of serialization concerns

### Capability System

The component system uses a capability-based approach to avoid code duplication:

1. **Base Capabilities**: All components inherit from `ZBaseComponent`
2. **Mixin Implementation**: Capabilities are implemented through mixin classes
3. **Flag System**: Components are flagged with their capabilities during construction
4. **Type Safety**: Generic types ensure compile-time safety

This architecture provides a flexible, type-safe, and maintainable foundation for building complex component-based systems in the Zernikalos Engine.
