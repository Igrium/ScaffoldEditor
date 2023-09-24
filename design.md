# Scaffold 2 Design Notes

## Minecraft Integration

- Scaffold 1 was written as a standalone codebase. When it was planned for the program to be completely standalone on JME, this made sense. However, once it was integrated with Minecraft, it became a mess.
  
- New system should *not* attempt to abstract away Minecraft types. Classes such as `BlockState` will be directly used by the codebase.
  
- Compiler can now use native world serialization to improve compatibility.
  
- Keep the server-client separation from Minecraft.
  
  - Allows for remote clients to connect for collaboration.
    
- "Game Mode" allows you to explore (and possibly edit) the level without the Scaffold UI.
  

## Levels

- Scaffold entities should be renamed to avoid confusion with Minecraft entities. Possibilities include:
  
  - `item`
    
  - `unit`
    
  - `article`
    
  - `object`
    
- Instead of telling entities to compile all their blocks at once, there should be a "query" system that queries what block that entity believes should be there.
  
  - Allows for multi-threaded compiling
    
  - Still inform the entity when compiling begins so it can setup a cache
    
- Levels should be coded in a way such that they can be loaded and unloaded in chunks, just like Minecraft worlds
  
  - Some schematic formats should support this too
    
  - Cache the compiled level on disk somehow?
    
- Entities do not store a map of all their attributes. Instead, read-only fields with `Attribute` (generic) objects are accessed via reflection. The `Attribute` interface will contain a getter and setter for the attribute value which is called whenever they're changed.
  
  - Attribute type must implement `XMLSerializable` for level serialization
    
  - Option to override the entity's serialization behavior using `toXML` and `fromXML` methods.
    

## Projects

- With the addition of custom dimensions, it may be possible to distribute multiple levels in one world.
  
  - Needs more research
