# rootkt

A pure Kotlin library for reading CERN ROOT files — no C++ bindings, no JNI, no ROOT installation required.

## Why

ROOT files are the standard data format for high-energy physics experiments (LHC, FCC, etc.). Existing JVM solutions either wrap the C++ ROOT library or are unmaintained. rootkt reads the binary format directly, making it suitable for pure JVM environments and scientific pipelines that can't carry a C++ dependency.

## Status

Early development. Currently supports:

- Parsing the ROOT file header (`TFile`)
- Parsing and walking `TKey` records (the file's index layer)
- Detection of compressed payloads and large files (>2 GB)

## Structure

```
rootkt-core         # byte buffer primitives
rootkt-format       # binary format: header, TKey parsing and walking
rootkt-model        # data classes for ROOT objects
rootkt-streamer     # TStreamerInfo deserialization
rootkt-tree         # TTree / TBranch / basket reading
rootkt-compression  # zlib, lz4, zstd decompression
rootkt-registry     # class name → streamer lookup
rootkt-io           # high-level file API
rootkt-runtime      # public entry point
rootkt-experimental # sandbox
```

## Quick start

```kotlin
val bytes = File("data.root").readBytes()
val buf = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)

val header = buf.parseRootHeader()
val keys = buf.walkKeys(header.begin, header.end)

keys.forEach { println("${it.className} ${it.name};${it.cycle}") }
```

## References

- [ROOT file format specification](https://root.cern/doc/v628/header.html)
- [ROOT data record format](https://root.cern/doc/v628/datarecord.html)
- [uproot](https://github.com/scikit-hep/uproot5) — Python reference implementation
- [groot](https://github.com/go-hep/hep/tree/main/groot) — Go reference implementation