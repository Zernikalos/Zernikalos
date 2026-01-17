# Changelog

All notable changes to the Zernikalos Engine will be documented in this file.

## [v0.7.0](https://github.com/Zernikalos/Zernikalos/releases/tag/v0.7.0) (2026-01-17)

### Added

- 🎸 New texture field implemented on Metal ([fe242c647463088](https://github.com/Zernikalos/Zernikalos/commit/fe242c64746308818089d8c74d9d2befb938d6ef))
- 🎸 New fields for texture rendering, ([0e0ebeadc04d60b](https://github.com/Zernikalos/Zernikalos/commit/0e0ebeadc04d60ba2a9ac96f237ecc97eef2ecb6))
- 🎸 Support KeyBoard State Pressed state handling ([c301734277c9b77](https://github.com/Zernikalos/Zernikalos/commit/c301734277c9b776ae430a135e3125ce485ba022))
- 🎸 Add basic support for keyboard+mouse ([5e3457d0fd145e1](https://github.com/Zernikalos/Zernikalos/commit/5e3457d0fd145e13c2020a583e56e2b81764a6f7))
- 🎸 First event system design proposal ([6b49956c721385f](https://github.com/Zernikalos/Zernikalos/commit/6b49956c721385f2d75a9427259d3ce9aa17b837))

### Fixed

- 🐛 Fixing releaseCommit task behavior ([b31487fb90fdb22](https://github.com/Zernikalos/Zernikalos/commit/b31487fb90fdb223df8b239792f5d9fc6254b9c0))
- 🐛 Error while retrieving version on cocoa config ([f2cb512d471867f](https://github.com/Zernikalos/Zernikalos/commit/f2cb512d471867fe342500a2d38846ceb5036007))


### Changed

- 💡 Merging tasks into one ([d0c82fb848a46a0](https://github.com/Zernikalos/Zernikalos/commit/d0c82fb848a46a098c89ae60d669920a03aa7981))
- 💡 Improving texture format ([e8a99438e4ac8bc](https://github.com/Zernikalos/Zernikalos/commit/e8a99438e4ac8bc11311f8002246007ad659de17))
- 💡 SceneStateHandler with optional methods ([8b3cdd92c31473f](https://github.com/Zernikalos/Zernikalos/commit/8b3cdd92c31473f6cb9c0c512bfc509b0acdc5a4))
- 💡 Including event handling in the main loop process ([da7674569d61635](https://github.com/Zernikalos/Zernikalos/commit/da7674569d61635bf3ea3c516337d5f5fefafff8))
- 💡 Creation of a event manager for cleaner ZObject interface ([3fe00b481eb493d](https://github.com/Zernikalos/Zernikalos/commit/3fe00b481eb493d9f2bf254fba5d0bc585f81aa7))
- 💡 Changing public API, onRender to onUpdate ([b9757adb92e24ad](https://github.com/Zernikalos/Zernikalos/commit/b9757adb92e24ad85b43b9fb2c18d3d352241af5))

### Documentation

- ✏️ Improving scripting and release documentation ([40f3e38052edafc](https://github.com/Zernikalos/Zernikalos/commit/40f3e38052edafcc6460fa6c7a095533c1f3168e))





### Chore

- 🤖 Replacing changelog generator for a better pick ([daf650ed42fd1c0](https://github.com/Zernikalos/Zernikalos/commit/daf650ed42fd1c09df86c25c7b557bc2e5bd1f40))
- 🤖 Adding changelog generator ([0b502f1f6d0c4a5](https://github.com/Zernikalos/Zernikalos/commit/0b502f1f6d0c4a538e7c7b3fe215bd3afb553ca7))
- 🤖 Improvements on Python scripts ([b2233fe1d14b585](https://github.com/Zernikalos/Zernikalos/commit/b2233fe1d14b58533eb6981b3f784dc86074967a))
- 🤖 Improving gradle tasks ([a7fe7d3ae79f8ac](https://github.com/Zernikalos/Zernikalos/commit/a7fe7d3ae79f8ac940b113c8db70c80505fa71fb))
- 🤖 Publish next release ([2705f95d5e1edf0](https://github.com/Zernikalos/Zernikalos/commit/2705f95d5e1edf059eec9164f1e63689d8899fb6))
- 🤖 Improving CI performance ([b1af6cda2bae8f7](https://github.com/Zernikalos/Zernikalos/commit/b1af6cda2bae8f7b29103b326e0095c81d396b0e))
- 🤖 Upgrading Kotlin version to 2.3.0 ([96ba42eaf396ade](https://github.com/Zernikalos/Zernikalos/commit/96ba42eaf396ade99ccd3942f34a8def1f09efe7))
- 🤖 Upgrading Kotlin version ([f22ed05bcf77660](https://github.com/Zernikalos/Zernikalos/commit/f22ed05bcf77660071fd0f41c24045a368efefac))
- 🤖 Removal of old scripts ([047df0c79b28b0c](https://github.com/Zernikalos/Zernikalos/commit/047df0c79b28b0c0c5956fe6576ce018940aa4d5))
- 🤖 Extra improvements ([0e9d5f20c3c74bd](https://github.com/Zernikalos/Zernikalos/commit/0e9d5f20c3c74bd37c9cda6f053018445ade6b6f))
- 🤖 Improve code sharing among publish scripts ([818949a80260a95](https://github.com/Zernikalos/Zernikalos/commit/818949a80260a9520fc564c908731309423df6df))
- 🤖 Creating a new BaseBuild class for publishing scripts ([c14bc1cc586b63a](https://github.com/Zernikalos/Zernikalos/commit/c14bc1cc586b63ab29b2d542bfdc91f898b64737))
- 🤖 Refactoring publishing scripts ([a507a60584114c7](https://github.com/Zernikalos/Zernikalos/commit/a507a60584114c7ec738ef69f1dc228010e9791d))
- 🤖 Publish scripts improvements ([84bf873ca941662](https://github.com/Zernikalos/Zernikalos/commit/84bf873ca9416627d186d5cca79251eba51634f6))

## [v0.6.0](https://github.com/Zernikalos/Zernikalos/releases/tag/v0.6.0) (2025-12-06)

### Added

- 🎸 WebGPU BufferContent and BufferKey renderer ([377853810a212ef](https://github.com/Zernikalos/Zernikalos/commit/377853810a212ef47a828982da5528f8714653f6))
- 🎸 Metal BufferContent and BufferKey renderer ([d55e74ff04a78d7](https://github.com/Zernikalos/Zernikalos/commit/d55e74ff04a78d70e124c68ca65614ed178398c8))
- 🎸 Ogl BufferContent and BufferKey renderer ([ae6948550ea6c50](https://github.com/Zernikalos/Zernikalos/commit/ae6948550ea6c50b4ef6ef0a6a36a907b10b067c))
- 🎸 Exposing interleave method on Buffer ([fb9dc771f5f4fb8](https://github.com/Zernikalos/Zernikalos/commit/fb9dc771f5f4fb825573f86462536183a7de3713))
- 🎸 Upgrade ZKO to v0.13.0 ([9757e77ddbdf1a7](https://github.com/Zernikalos/Zernikalos/commit/9757e77ddbdf1a77eacf48ebc2e9d1a141b71974))
- 🎸 Support mesh references from ZKO files ([53e21eace3edb6e](https://github.com/Zernikalos/Zernikalos/commit/53e21eace3edb6e9bb7feeb9a92f2ebc5c031297))

### Fixed

- 🐛 WebGPU wrongly align data size for buffers ([da2067c8daf650f](https://github.com/Zernikalos/Zernikalos/commit/da2067c8daf650f3cc213da0e2f6e0e3152a5e9a))


### Changed

- 💡 Setting ZBufferKey+Content as independent components ([deb23586cfee74b](https://github.com/Zernikalos/Zernikalos/commit/deb23586cfee74bec17a0e9bb5848d070d4267df))
- 💡 Storing ZBufferKey and ZBufferContent as part of ZBuffer ([708b8342541eb98](https://github.com/Zernikalos/Zernikalos/commit/708b8342541eb987bd4f27e1db47efbc74950fd1))
- 💡 Renaming ZRawBuffer to ZBufferContent ([58ca6d5ad707121](https://github.com/Zernikalos/Zernikalos/commit/58ca6d5ad707121bf639859c5c875c63dad4e2ed))
- 💡 Temporal workaround showing how to setup buffers for interleave ([37410adfcf05c7e](https://github.com/Zernikalos/Zernikalos/commit/37410adfcf05c7e67bbb23a776a2e8fc1ff0de4c))






### Chore

- 🤖 Upgrade fast build CI code ([0f0bed86523f3fb](https://github.com/Zernikalos/Zernikalos/commit/0f0bed86523f3fbe9aeeb394cb897c599dbadc2f))
- 🤖 Updating Kotlin version ([ed3a7c8807ce442](https://github.com/Zernikalos/Zernikalos/commit/ed3a7c8807ce4425794eaf01860a85006ad8983f))
- 🤖 Updating some dependencies ([f8a881ea06a979e](https://github.com/Zernikalos/Zernikalos/commit/f8a881ea06a979e932ff043b1e5c2a9eb44ee9a1))

## [v0.5.0](https://github.com/Zernikalos/Zernikalos/releases/tag/v0.5.0) (2025-09-18)

### Added

- 🎸  Adding dispose method for surface views ([77cb256f7cd9def](https://github.com/Zernikalos/Zernikalos/commit/77cb256f7cd9defe35cc9017734394c67759fe36))




### Documentation

- ✏️ Updating release process documentation ([392b3faa389f1ef](https://github.com/Zernikalos/Zernikalos/commit/392b3faa389f1ef9bcc0b5062716187728baba2f))
- ✏️ Improving documentation generation ([2a71cff436bc941](https://github.com/Zernikalos/Zernikalos/commit/2a71cff436bc941f5d318de014a4e1b57aa27d9a))





### Chore

- 🤖 Updating publish npm script ([6013de78a44e7d6](https://github.com/Zernikalos/Zernikalos/commit/6013de78a44e7d61d90e7078c57ba6d5931b0e77))
- 🤖 Updating dependencies ([630d04fba488a81](https://github.com/Zernikalos/Zernikalos/commit/630d04fba488a81a30bf003abd31e28b3ac8a39e))

## [v0.4.1](https://github.com/Zernikalos/Zernikalos/releases/tag/v0.4.1) (2025-09-07)


### Fixed

- 🐛 Adding pendingResize flag so this resizing does not trigger so frequently ([3df3a01a953f8a2](https://github.com/Zernikalos/Zernikalos/commit/3df3a01a953f8a2d37c1b161df8c7a76877744ad))
- 🐛 Fix error for resizing canvas on webgpu ([af0d64c375a1330](https://github.com/Zernikalos/Zernikalos/commit/af0d64c375a1330112cd32f427b6660726794d14))
- 🐛 Improving the serialization for RN plugin ([3d0fa17e90ed3db](https://github.com/Zernikalos/Zernikalos/commit/3d0fa17e90ed3db934134596aeb58d105abc457d))


### Changed

- 💡 Improving code structure for the fix ([fde655237b966b1](https://github.com/Zernikalos/Zernikalos/commit/fde655237b966b1a7405a009d2605e8a87ce809e))
- 💡 Component refactor - Adding ZOmniComponent ([34a53a245e1ce56](https://github.com/Zernikalos/Zernikalos/commit/34a53a245e1ce56b26ed5096901d998905a1433c))
- 💡 Small changes for RN compatibility ([0349af27c000fdf](https://github.com/Zernikalos/Zernikalos/commit/0349af27c000fdffe819d209a511cb5be0fc31a4))

### Documentation

- ✏️ Adding documentation for the component classes ([917abf13f230870](https://github.com/Zernikalos/Zernikalos/commit/917abf13f230870db8cb324b57cfb8dbca72a7dc))
- ✏️ Adding documentation for the UI classes ([f283eb6a0f4f72f](https://github.com/Zernikalos/Zernikalos/commit/f283eb6a0f4f72ffbef09d49d99479a6ab528a1e))
- ✏️ Adding documentation for scripts ([9c2d5566bbb70ea](https://github.com/Zernikalos/Zernikalos/commit/9c2d5566bbb70ea561118db316317bd3ad34e65f))





### Chore

- 🤖 Removing .sh scripts ([a168a75032f4bea](https://github.com/Zernikalos/Zernikalos/commit/a168a75032f4beaf6342a3f45b03dfcca5b5e239))
- 🤖 Adding a versioning python script ([69e8b1fbbedd93d](https://github.com/Zernikalos/Zernikalos/commit/69e8b1fbbedd93d36447a9fd0ca109ce05b0ec58))
- 🤖 Publishing scripts in Python ([fe007918aac386b](https://github.com/Zernikalos/Zernikalos/commit/fe007918aac386b9372725ffdf3b75f01448c9b7))

## [v0.4.0](https://github.com/Zernikalos/Zernikalos/releases/tag/v0.4.0) (2025-08-27)

### Added

- 🎸 Exposing engine+zko versions on main objects ([a8f524405d7b00b](https://github.com/Zernikalos/Zernikalos/commit/a8f524405d7b00bce5a2e6d42ce92f3a537f2f9c))
- 🎸 Adding support for Phong on WebGPU ([e24d8c2445d747f](https://github.com/Zernikalos/Zernikalos/commit/e24d8c2445d747fa1fd026091ca2ba43ad65e711))
- 🎸 Adding support for Phong on Metal ([b687b6a43107f24](https://github.com/Zernikalos/Zernikalos/commit/b687b6a43107f2475154ef0a09c948785ade501f))
- 🎸 Upgrade ZKO to v0.12.0 ([9a89d3308ca504d](https://github.com/Zernikalos/Zernikalos/commit/9a89d3308ca504dc764654236665e34fe321cd96))
- 🎸 Add support for Phong material on Android ([33dc3e50345f1ab](https://github.com/Zernikalos/Zernikalos/commit/33dc3e50345f1ab2beabb0dc348191b5db966679))

### Fixed

- 🐛 Fixes on material and shaders ([507fc31b4f942f3](https://github.com/Zernikalos/Zernikalos/commit/507fc31b4f942f3bb531f2a8ac56b32168911516))
- 🐛 Fixing and improving some operations in math module ([c109cd277388c78](https://github.com/Zernikalos/Zernikalos/commit/c109cd277388c7830e54af35702d6973fba14cd9))
- 🐛 Building a better way to compute binding points for uniform blocks ([163348f0b1f86de](https://github.com/Zernikalos/Zernikalos/commit/163348f0b1f86de368d7db59062eaa0835c3c0e0))
- 🐛 Error on child deletion ([28a30c484103367](https://github.com/Zernikalos/Zernikalos/commit/28a30c484103367ecd91d91891f87a68c4a286ec))
- 🐛 Error on WebGPU renderPass.end call not allowing to render multiple models ([e7cd2fcef485d41](https://github.com/Zernikalos/Zernikalos/commit/e7cd2fcef485d41df311a2c04d495fb183974329))
- 🐛 Error on GL Uniform blocks due to global state corruption ([3023af6d9a76009](https://github.com/Zernikalos/Zernikalos/commit/3023af6d9a76009fc5d4d5717662f2ca604ebc73))
- 🐛 Corrections for multiple issues on Ogl states ([b8383afa0df1112](https://github.com/Zernikalos/Zernikalos/commit/b8383afa0df111231d2169a383bb9368be87a806))



### Documentation

- ✏️ Updating documentation about the material system ([9e7260afb53f8e6](https://github.com/Zernikalos/Zernikalos/commit/9e7260afb53f8e6a537319a97387c894650fa175))
- ✏️ Add extra documentation ([7e8b76bf1124f99](https://github.com/Zernikalos/Zernikalos/commit/7e8b76bf1124f99138977abf6dcc778cc3e65f35))


### Tests

- 🧪 Trying to improve MathTestUtils ([ec24efa39676d95](https://github.com/Zernikalos/Zernikalos/commit/ec24efa39676d95ffa2900f85ba5350a9d48e9cb))



### Chore

- 🤖 Unification of build and release workflows ([42e1ba0fe21048c](https://github.com/Zernikalos/Zernikalos/commit/42e1ba0fe21048ccfe6b4824320a825372bec133))

## [v0.3.0](https://github.com/Zernikalos/Zernikalos/releases/tag/v0.3.0) (2025-08-17)

### Added

- 🎸 Add support for PBR shader on WebGPU ([e33540991d5a7b8](https://github.com/Zernikalos/Zernikalos/commit/e33540991d5a7b82c98a0844aa2110b4eeb32b19))
- 🎸 Add support for PBR shader on Metal ([2f9f5e61d95d09a](https://github.com/Zernikalos/Zernikalos/commit/2f9f5e61d95d09a2632bc954147e06ac8f40197d))
- 🎸 Add support for PBR shader on OpenGL ([5031147901f59c2](https://github.com/Zernikalos/Zernikalos/commit/5031147901f59c2d888a7587f1fccbd8ccc5aee8))
- 🎸 Add PBR material to Android shaders ([2933ab8e992e0de](https://github.com/Zernikalos/Zernikalos/commit/2933ab8e992e0de04df74141c739001c91ffa448))
- 🎸 Creation of PBR based material data ([65e8c86f04459d5](https://github.com/Zernikalos/Zernikalos/commit/65e8c86f04459d51b41d7a4e6ff5967c25cef931))
- 🎸 Allowing skinning on webpgu ([046d91409669592](https://github.com/Zernikalos/Zernikalos/commit/046d91409669592c9ce608aaf5644b5b5a7145ea))
- 🎸 Adding texture support ([4fa3b13c238605c](https://github.com/Zernikalos/Zernikalos/commit/4fa3b13c238605c7369204310666c7ff82cc6fad))
- 🎸 Passing all buffers to gpu ([823239e0901ba9e](https://github.com/Zernikalos/Zernikalos/commit/823239e0901ba9ede32c08ac8a83b9ad1e2c1ae7))
- 🎸 Enabling passing data from uniform blocks ([56f050755b8f10f](https://github.com/Zernikalos/Zernikalos/commit/56f050755b8f10fe1d6c4d84a66137e9eebcce5b))
- 🎸 Migrating render pipeline descriptor into the ZModel Renderer ([a44939f5b6355ae](https://github.com/Zernikalos/Zernikalos/commit/a44939f5b6355aea3bee267dfc42bfc75f4009cf))
- 🎸 Migrating depth + frame Texture into viewport ([19e37728f31942b](https://github.com/Zernikalos/Zernikalos/commit/19e37728f31942b3b82938968823f160e1b6fd35))
- 🎸 Migrating shader initialization into its own classes ([098be54b1cd25a2](https://github.com/Zernikalos/Zernikalos/commit/098be54b1cd25a236911ecc55898a75d39f1d116))
- 🎸 Migrating vertexBufferLayouts into mesh ([988c02ad3bd39fe](https://github.com/Zernikalos/Zernikalos/commit/988c02ad3bd39fe3fc96baac8c7069d7fe1dee4d))
- 🎸 Moving code into required render classes ([3bc6a9007ef9d7e](https://github.com/Zernikalos/Zernikalos/commit/3bc6a9007ef9d7e5291b137a3a4dc4732460bf9d))
- 🎸 Basic support for WebGPU ([54887b5e0086de1](https://github.com/Zernikalos/Zernikalos/commit/54887b5e0086de16ac1958d2d2de82ebf9966a89))
- 🎸 Upgrade ZKO to v0.10.0 ([82bc36b656aef07](https://github.com/Zernikalos/Zernikalos/commit/82bc36b656aef079f968c3416cb5667db49b262f))
- 🎸 Using boneId as identification for bones in pose maps ([02ea7718e091a26](https://github.com/Zernikalos/Zernikalos/commit/02ea7718e091a26ec85920d07c2eeb2f54548928))
- 🎸 Vertex skinning through UniformBlocks on Ogl ([254fb3f1c260602](https://github.com/Zernikalos/Zernikalos/commit/254fb3f1c2606027355ba37192d3da05cc6508da))
- 🎸 Supporting vertex skinning on Metal ([85d5f1abb6dfc2f](https://github.com/Zernikalos/Zernikalos/commit/85d5f1abb6dfc2fde885ec6f5a7c9a62f69cb962))
- 🎸 Uniforms in Metal without hardcoded ids or index ([2da19da6355eb5b](https://github.com/Zernikalos/Zernikalos/commit/2da19da6355eb5b417ff6e490b06ac383b211367))
- 🎸 UniformBlock working for Metal API ([b6594f267a79c51](https://github.com/Zernikalos/Zernikalos/commit/b6594f267a79c5114525be94af47b558ed4bad36))
- 🎸 Uniform blocks working on OpenGL ([8f99000a0fd1108](https://github.com/Zernikalos/Zernikalos/commit/8f99000a0fd110868d9240c57acec321fada8362))
- 🎸 Adding byteSize field to ZAlgebraObject ([29080a4ad8c7f8f](https://github.com/Zernikalos/Zernikalos/commit/29080a4ad8c7f8ff9806dea23574cd17dca99714))
- 🎸 Adding byteArray field to all ZAlgebraObject ([15d45c7e5bad3ea](https://github.com/Zernikalos/Zernikalos/commit/15d45c7e5bad3eacf4e03f1fc7611d8a12ea8902))
- 🎸 Upgrade ZKO to v0.8.0 ([67d2b9ec6366233](https://github.com/Zernikalos/Zernikalos/commit/67d2b9ec63662339b208f313fdab89d2e4af543c))
- 🎸 Adding basic animation player ([8dfd2b4da244de3](https://github.com/Zernikalos/Zernikalos/commit/8dfd2b4da244de3634632506a31c503ff585516c))
- 🎸 Upgrade ZKO to v0.7.0 ([8c8280e22ab58fa](https://github.com/Zernikalos/Zernikalos/commit/8c8280e22ab58fab892b5a244ab8edd5953b7b5d))
- 🎸 Adding support for ZSKinning component ([b77e4d21fe266c1](https://github.com/Zernikalos/Zernikalos/commit/b77e4d21fe266c1df0d85a141246e53ad9b7cd58))
- 🎸 Implementation of animation tracks ([af94d6b03746377](https://github.com/Zernikalos/Zernikalos/commit/af94d6b037463775182be61e784b6c433d72a7e9))
- 🎸 Allowing to perform NPM publish from gradle ([d80063cc6c2e15c](https://github.com/Zernikalos/Zernikalos/commit/d80063cc6c2e15c5c6d0a56d1164e229c245f50f))
- 🎸 Adding custom Android Surface view for easier integration ([3fc24280931350d](https://github.com/Zernikalos/Zernikalos/commit/3fc24280931350d9adc9e23e47c65e18b0f65a49))
- 🎸 Supporting version ranges in ZKO after adding semver class ([a3db9cefa868966](https://github.com/Zernikalos/Zernikalos/commit/a3db9cefa868966f11c947d5e313317737f5ee72))
- 🎸 Allowing to configure different draw modes ([e585d5c4e4f3b4c](https://github.com/Zernikalos/Zernikalos/commit/e585d5c4e4f3b4c30704c4d5c44b55897122b575))
- 🎸 Adding ZEuler class ([920132906df7ad8](https://github.com/Zernikalos/Zernikalos/commit/920132906df7ad84f4cd5795f56f1b572d7dc67e))
- 🎸 Upgrade ZKO to 0.5.0 ([f3f601b84aa74b2](https://github.com/Zernikalos/Zernikalos/commit/f3f601b84aa74b252cc93c456dceb6da03a1f2e7))
- 🎸 Add version to stats ([b36c68206422369](https://github.com/Zernikalos/Zernikalos/commit/b36c68206422369507a6a4d6b32ac6de928af5b1))
- 🎸 Support for v0.4.0 of ZkoFormat ([0f947c45893be6b](https://github.com/Zernikalos/Zernikalos/commit/0f947c45893be6b13eca08b9dbdb4e3956d732b7))
- 🎸 Splitting uniforms in different collections for processing ([eb6a45d2076df42](https://github.com/Zernikalos/Zernikalos/commit/eb6a45d2076df4225c79039dda275908c650908a))
- 🎸 Basic structure for supporting UniformBuffers ([f18111b6a00ce7b](https://github.com/Zernikalos/Zernikalos/commit/f18111b6a00ce7b9df747470ba48cfeff8b14c51))
- 🎸 Simple CocoaPod pod generation ([9c3cdda46eef2a0](https://github.com/Zernikalos/Zernikalos/commit/9c3cdda46eef2a047885d6e36a11f66d38c5fc81))
- 🎸 Changing the shader source generator for a function ([e741bc4e8d72c92](https://github.com/Zernikalos/Zernikalos/commit/e741bc4e8d72c926b85e9d0003bed022fdcc3e8f))
- 🎸 Adding pan method to ZTransform ([c5f85ae8cde4e98](https://github.com/Zernikalos/Zernikalos/commit/c5f85ae8cde4e98d7fd6ae5739b5772f84c9b4be))
- 🎸 Support updating local axis update ([65e2e6467017de0](https://github.com/Zernikalos/Zernikalos/commit/65e2e6467017de0b6900a3406e2289522f241b02))
- 🎸 Add ZColor class for RGBA color representation ([50ebb3f2cdd5568](https://github.com/Zernikalos/Zernikalos/commit/50ebb3f2cdd5568b2252478db9ce2b6acb7d09d7))
- 🎸 ZVector2 class added ([3c1b9203cf452f2](https://github.com/Zernikalos/Zernikalos/commit/3c1b9203cf452f2f181497fe0bd757996620388b))
- 🎸 Add extra fields to animation ([d714fdffc02bf45](https://github.com/Zernikalos/Zernikalos/commit/d714fdffc02bf45dc899bd23d95cc3de3099b8a8))
- 🎸 First building blocks for vertex skinning ([94e8caac87f4c1c](https://github.com/Zernikalos/Zernikalos/commit/94e8caac87f4c1c7e95cdfb41d397bade226ae56))
- 🎸 Add interpolation methods to some action related classes ([380df1e22948ac3](https://github.com/Zernikalos/Zernikalos/commit/380df1e22948ac311ef661701e2377805896d563))
- 🎸 Bind, InverseBind and Bone matrix addition ([1cf53f0737a90a1](https://github.com/Zernikalos/Zernikalos/commit/1cf53f0737a90a1914850ef224e7e89848249bdb))
- 🎸 Implement basic keyframe structure on engine ([1b9336eb0fdefc3](https://github.com/Zernikalos/Zernikalos/commit/1b9336eb0fdefc33f73d1a6f83bddd9b7c933ba7))
- 🎸 Add support for viewport resizing ([a059c3978127bba](https://github.com/Zernikalos/Zernikalos/commit/a059c3978127bbaded3112d9e1d3fe5dadb76812))
- **android** 🎸 Add support for viewport resizing ([5f10c40599f6af1](https://github.com/Zernikalos/Zernikalos/commit/5f10c40599f6af15937d07216e791a9c237c0cbf))
- **metal** 🎸 Add support for viewport resizing ([20a8e7df92f56b2](https://github.com/Zernikalos/Zernikalos/commit/20a8e7df92f56b2027512feec157671c7a09e956))
- **metal** 🎸 Load from file on Apple based environments ([ac57d7e6df95631](https://github.com/Zernikalos/Zernikalos/commit/ac57d7e6df95631c88e4b5d587a8f708c596dc41))
- 🎸 Add ZSearchIndex class for efficient ZObject retrieval ([756cc4430f2d341](https://github.com/Zernikalos/Zernikalos/commit/756cc4430f2d3412aa078d929a1532ea947bd457))
- 🎸 Support for flipped textures ([403d2cbaf55b15a](https://github.com/Zernikalos/Zernikalos/commit/403d2cbaf55b15a51f7d11edd99ffd7dfdb04fd0))
- 🎸 Allowing to use Zernikalos API on iOS ([21f5785a98f86b0](https://github.com/Zernikalos/Zernikalos/commit/21f5785a98f86b0e52aae8b4c90fe774855834b6))
- 🎸Add ZRenderer class to the hierarchy ([ebf165888e9841c](https://github.com/Zernikalos/Zernikalos/commit/ebf165888e9841cf85c97ef92cd36108c4fc1971))
- 🎸 Model with different renderers, for metal+ogl ([a20682903e0bbe9](https://github.com/Zernikalos/Zernikalos/commit/a20682903e0bbe9cfe4537778f5c87cca56e82a3))
- 🎸 Adding serialization support on ZComponents ([3c5dfe40e25525f](https://github.com/Zernikalos/Zernikalos/commit/3c5dfe40e25525fc9157132ca30ca6184db17ae2))
- 🎸 Adding simple stats class ([70888d78a726fdb](https://github.com/Zernikalos/Zernikalos/commit/70888d78a726fdb11f744fd2260febc70c8c44cc))
- 🎸 Improving the initialization process on the engine ([414aff4297ea81a](https://github.com/Zernikalos/Zernikalos/commit/414aff4297ea81af64432817d35e0f6260988ede))
- 🎸 Improve logs levels and adding settings for the engine ([e5a2d6182965ef5](https://github.com/Zernikalos/Zernikalos/commit/e5a2d6182965ef57935d2af376389561a30c294b))
- 🎸 Add support for shaders in the engine ([efa33c13684f60f](https://github.com/Zernikalos/Zernikalos/commit/efa33c13684f60fa94abe7c9d8adb7c807421d12))
- 🎸 Allowing basic references in components ([f2b11c5c55aa99c](https://github.com/Zernikalos/Zernikalos/commit/f2b11c5c55aa99cc0915ceb250fb4fb437131dfb))
- 🎸 Add logger adapters and some debugging logs ([773b0d42aa1088f](https://github.com/Zernikalos/Zernikalos/commit/773b0d42aa1088ffe5a16a17c4bbaedbe8f2ff34))
- 🎸 Add logger class ([83e40ab113fc525](https://github.com/Zernikalos/Zernikalos/commit/83e40ab113fc525f2a0be6447b0b920447e64824))
- 🎸 ZObjectType addition ([cb33b764a5342a8](https://github.com/Zernikalos/Zernikalos/commit/cb33b764a5342a890b6421fc124929095777f6ea))
- 🎸 Support shaders in engine for metal ([83471e3af41e09b](https://github.com/Zernikalos/Zernikalos/commit/83471e3af41e09b9dc278f47a457b40736e89136))
- 🎸 Preparing the use of textures in metal ([be23187c6260769](https://github.com/Zernikalos/Zernikalos/commit/be23187c6260769925626205f3b8e39ec2579fbe))
- 🎸 Basic support for shaders from the engine on metal ([f3d795f007a694b](https://github.com/Zernikalos/Zernikalos/commit/f3d795f007a694ba539dbf5ba114a8a94e43e8b2))
- 🎸 Add width and height to texture data ([7507c05518794fd](https://github.com/Zernikalos/Zernikalos/commit/7507c05518794fd025fa141737ee810871c877b6))
- 🎸 Add support for reference identifier in components ([33907592f6c9d3a](https://github.com/Zernikalos/Zernikalos/commit/33907592f6c9d3a93b25042dc2167812b0b3e4b5))
- 🎸 Add bind related matrices ([0917b70027067db](https://github.com/Zernikalos/Zernikalos/commit/0917b70027067dbca1566f81d4609dee6b8c3d07))
- 🎸 Add support for view matrix in the scenecontext ([000bcec0f7cede5](https://github.com/Zernikalos/Zernikalos/commit/000bcec0f7cede5ff6bf0b8f37f2df29784f3859))
- 🎸 Add support for bone matrices uniform generator ([0348bd65c6ed46b](https://github.com/Zernikalos/Zernikalos/commit/0348bd65c6ed46b9d6c69941049836cdf345bbe1))
- 🎸 Including the bones affecting the skinning ([d97dc66138200b7](https://github.com/Zernikalos/Zernikalos/commit/d97dc66138200b7786eacceeb99249b156885651))
- 🎸 Naive search in tree for bones ([39e20bcc54677da](https://github.com/Zernikalos/Zernikalos/commit/39e20bcc54677daee5016ef567ec0d233d688ddf))
- 🎸 Add support for bone indices ([7ca4f0c44a22273](https://github.com/Zernikalos/Zernikalos/commit/7ca4f0c44a22273bfc8bb72c7fd389286274a717))
- 🎸 Add skeleton support in model ([eca4591beefa2bd](https://github.com/Zernikalos/Zernikalos/commit/eca4591beefa2bdd02c90448450b3ca83203424b))
- 🎸 Skeleton as a basic component ([49ab2373cbfcc0b](https://github.com/Zernikalos/Zernikalos/commit/49ab2373cbfcc0b6181772ab9f441862649d67d8))
- 🎸 Load from file on Android and iOS ([6618fd06aba16f7](https://github.com/Zernikalos/Zernikalos/commit/6618fd06aba16f74fd9edfb7a22aa186b6a85046))
- 🎸 Really basic support for metal uniforms ([bff59f705b09d66](https://github.com/Zernikalos/Zernikalos/commit/bff59f705b09d668d26efe0c933105c91c29ec78))
- 🎸 Improving public API for components ([0d575fb6471b315](https://github.com/Zernikalos/Zernikalos/commit/0d575fb6471b315777dce7b2c820748a5df68c3c))
- 🎸 Adding a better support for DataType expression ([6655e660cfcc2b9](https://github.com/Zernikalos/Zernikalos/commit/6655e660cfcc2b9e876c3eb7529b236f0b2896a0))
- 🎸 Support for metal target on zernikalos ([5cef98406750055](https://github.com/Zernikalos/Zernikalos/commit/5cef9840675005512a0ad2e110b9e4a2fd75f60f))
- 🎸 First steps to support textures on web ([9451dde00473452](https://github.com/Zernikalos/Zernikalos/commit/9451dde00473452d999f28b6c2694f65a7c5db60))
- 🎸 Basic texture support for Android platform ([6c02511cd78d81a](https://github.com/Zernikalos/Zernikalos/commit/6c02511cd78d81ac2ca027ce329c3227c0e4e68b))
- 🎸 New buffer concept using a mix of Key + Buffer ([5983b17d39f011e](https://github.com/Zernikalos/Zernikalos/commit/5983b17d39f011eb46fd0f9a53e048c43ef2d968))
- 🎸 Adding support for all in one place buffer definition ([dcaa66aecd4f649](https://github.com/Zernikalos/Zernikalos/commit/dcaa66aecd4f649354bd5f7cbde8af62a1e62fdf))
- 🎸 Support of a subset of rendering commands for Metal ([daed3b7e75ee99e](https://github.com/Zernikalos/Zernikalos/commit/daed3b7e75ee99ef9e671455664c63b8a7202d0f))
- 🎸 Creation of Renderer classes in native module ([34974bd450d1934](https://github.com/Zernikalos/Zernikalos/commit/34974bd450d193450fad8b23dc15bfa740c43f01))
- 🎸 Exporting data type on ZAttributeKeys ([af68ff181f54912](https://github.com/Zernikalos/Zernikalos/commit/af68ff181f54912438d92ac4eab330fde7512592))
- 🎸 Support Camera import from zk file ([2fb1bbe144a64fa](https://github.com/Zernikalos/Zernikalos/commit/2fb1bbe144a64fadd7077a4a1a7e09d0a674e204))
- 🎸 Adding support for loading ZCameras from proto ([2fc780cea3bcbbd](https://github.com/Zernikalos/Zernikalos/commit/2fc780cea3bcbbd834743eefa3f50205568561da))
- 🎸 Uniform support for cameras related matrices ([d2f631866cc961d](https://github.com/Zernikalos/Zernikalos/commit/d2f631866cc961d04062b115979dfe57a240d2d8))
- 🎸 Addition of projection matrix and camera ([881b7b5a76f7216](https://github.com/Zernikalos/Zernikalos/commit/881b7b5a76f7216c5e4d2ec44db1c9cee358bb73))
- 🎸 Add identifiers to MrObject ([410bd3d9af5d8af](https://github.com/Zernikalos/Zernikalos/commit/410bd3d9af5d8af378e80ba9f60f526d119f67c5))
- 🎸 Support for Protobuf as a format file ([6813ac964f5364e](https://github.com/Zernikalos/Zernikalos/commit/6813ac964f5364e868fd48b83cc4c8b26715f4e6))
- 🎸 Adding support for MrTransform in CBOR format ([cda6b6750f37365](https://github.com/Zernikalos/Zernikalos/commit/cda6b6750f373658a5aac62563c66a1b561e2db4))
- 🎸 Binding generators + uniforms in a very naive way ([b22457c7e72bb89](https://github.com/Zernikalos/Zernikalos/commit/b22457c7e72bb894417ab38a7cc05363cb0b007f))
- Adding scene context ([d42490a47ff5ff1](https://github.com/Zernikalos/Zernikalos/commit/d42490a47ff5ff1ebe2cdfd0f805d6b8dcdcc1c9))
- 🎸 Adding android sample to the project ([bc3f5888d0570b0](https://github.com/Zernikalos/Zernikalos/commit/bc3f5888d0570b0c0d8c0ef22b414bc5fcdf865a))
- 🎸 Basic structure for LWJGL support ([8bb25ba17bb8059](https://github.com/Zernikalos/Zernikalos/commit/8bb25ba17bb805954a3b7801eecb90613f0ba075))
- 🎸 Adding MrTransform essential functionalities ([7d6e33420215e21](https://github.com/Zernikalos/Zernikalos/commit/7d6e33420215e21b3a9a595d2ac255ed9dd0c877))
- 🎸 Successfully sending uniforms to the gpu ([8b707caa99c584c](https://github.com/Zernikalos/Zernikalos/commit/8b707caa99c584c6fbc1e111563832e7e130833f))
- 🎸 Setting all the ground for uniforms access ([11f5a5ecf24160d](https://github.com/Zernikalos/Zernikalos/commit/11f5a5ecf24160d3e2feb6d7a67699ed14a34e30))

### Fixed

- 🐛 Error handling could trigger NPE ([d223ab4e91b430b](https://github.com/Zernikalos/Zernikalos/commit/d223ab4e91b430b10a0df669275e6d8131cec5d3))
- 🐛 Fixes and improvements on the PBR material setup ([f1f9f24211b4514](https://github.com/Zernikalos/Zernikalos/commit/f1f9f24211b451470c7d5a53153dcc5973d6b1cf))
- 🐛 Error on JS Build ([f9a0254ed80db70](https://github.com/Zernikalos/Zernikalos/commit/f9a0254ed80db70c7eb4ca101bd7cdd0420723c9))
- 🐛 Small fixes and corrections ([e4e1ca8b42fdf1f](https://github.com/Zernikalos/Zernikalos/commit/e4e1ca8b42fdf1f407b23c3b63973f386c61cd02))
- 🐛 Small fixes and corrections on matrix computations ([1e610f11e5af0bd](https://github.com/Zernikalos/Zernikalos/commit/1e610f11e5af0bd1071ce9bbc0cb2f746f70770b))
- 🐛 Error when building for JS ([eee2f7be6872e63](https://github.com/Zernikalos/Zernikalos/commit/eee2f7be6872e630cc90e465c3c20dc00e26b343))
- 🐛 Minor issues fixing ([c016052a55816a3](https://github.com/Zernikalos/Zernikalos/commit/c016052a55816a3121f88f898f420e0214c4cacc))
- 🐛 Correcting error on buffer specification variable ([2cfa00df5ca2182](https://github.com/Zernikalos/Zernikalos/commit/2cfa00df5ca2182e7974f3827b4392b11c72e0e5))
- 🐛 Correcting endianness used in toByteArray methods ([5ec23e4e3e452e5](https://github.com/Zernikalos/Zernikalos/commit/5ec23e4e3e452e59615e4acc0c94f8626c6eec4f))
- 🐛 Errors on Metal Rendering ([d828016c95d92e1](https://github.com/Zernikalos/Zernikalos/commit/d828016c95d92e17af2e1adb69ef55ab2012f356))
- 🐛 Build for Apple platform ([7398c6d8e546b79](https://github.com/Zernikalos/Zernikalos/commit/7398c6d8e546b794556663c56e142da95582e022))
- 🐛 ZBoneTransform was taking default global values when null is present ([81350fdcef98c83](https://github.com/Zernikalos/Zernikalos/commit/81350fdcef98c83c19cd1256317f2ad63b04d153))
- 🐛 Small fixes on ZTexture id ([c2f57f3c6f09424](https://github.com/Zernikalos/Zernikalos/commit/c2f57f3c6f09424462cc1e7b1663ad303dc2a2f2))
- 🐛 Removing the need to split indexBuffer initialization and binding on Ogl ([706b7d428a0971e](https://github.com/Zernikalos/Zernikalos/commit/706b7d428a0971e70d0d3b970944609b2aef071e))
- 🐛 Unable to publish maven packages after generateVersionConstants addition ([f29e25ba18d285d](https://github.com/Zernikalos/Zernikalos/commit/f29e25ba18d285df9db7bfba0d1e31ccca4ddfa5))
- 🐛 Set of small fixes added ([3075f2994de744b](https://github.com/Zernikalos/Zernikalos/commit/3075f2994de744b623e3839d5cc91da7135e0d7a))
- 🐛 Fixing error on Apple platform related to initial perspective not applying viewport size ([c179c5e05bc8db0](https://github.com/Zernikalos/Zernikalos/commit/c179c5e05bc8db0863c2e05b056e4febd87f2498))
- 🐛 Fixes on rotation compute on ZTransform ([a9ac6015438432c](https://github.com/Zernikalos/Zernikalos/commit/a9ac6015438432c5e380df27135c191b4a35afd1))
- 🐛 Small fixes for types around ([da3ab5f8f897372](https://github.com/Zernikalos/Zernikalos/commit/da3ab5f8f897372ec25735a9af8094d7a136facf))
- 🐛 Fixing error setting buffers on Metal API ([e676122aff79ae9](https://github.com/Zernikalos/Zernikalos/commit/e676122aff79ae9bcb6d2982ab55095537eb008c))
- 🐛 Error on matrix translation computation ([3138045edb9ffdf](https://github.com/Zernikalos/Zernikalos/commit/3138045edb9ffdfe6b607f0f4f1272927d47dd68))
- 🐛 Fix error on serialization when using arrays ([250e34dc1d2b0ae](https://github.com/Zernikalos/Zernikalos/commit/250e34dc1d2b0ae54305ec450d4d53f60c90a591))
- 🐛 When no color is set nothing is being drawn ([6cce22172b8c019](https://github.com/Zernikalos/Zernikalos/commit/6cce22172b8c0190d938bf2b6ee454c126eff5d3))
- 🐛 Error passing data on metal uniforms ([eb5477d99b2312d](https://github.com/Zernikalos/Zernikalos/commit/eb5477d99b2312d934cd228a4471514d3f80a28b))
- 🐛 Correction for refId generation when used on builder ([fe5ec805d6a98b6](https://github.com/Zernikalos/Zernikalos/commit/fe5ec805d6a98b657e0f9dba24683fe6b9abdfe2))
- 🐛 Error not applying bone hierarchy ([6da9461f48cfa90](https://github.com/Zernikalos/Zernikalos/commit/6da9461f48cfa9082b5d6682f71b7b2bc7a7ceb2))
- 🐛 Imports optimization ([6ede165fe8de45f](https://github.com/Zernikalos/Zernikalos/commit/6ede165fe8de45f2b18304add670186906479a00))
- 🐛 Minor fixes and general improvements ([575393d9699510e](https://github.com/Zernikalos/Zernikalos/commit/575393d9699510ed2540cb35cbba1725841a891c))
- 🐛 Correction of a small bug in refId generation ([eea1c7b91d51cca](https://github.com/Zernikalos/Zernikalos/commit/eea1c7b91d51cca7562eff237bc9c1b367b603cd))
- 🐛 Exposing ZComponent as a JS element ([c715b00332f1bfc](https://github.com/Zernikalos/Zernikalos/commit/c715b00332f1bfc70c416b477ced179752d89bb7))
- 🐛 Fix an error when using a texture and color in the same mesh ([9762d42d686ae42](https://github.com/Zernikalos/Zernikalos/commit/9762d42d686ae429bc79d3ce9411847c12a37bc1))
- 🐛 Reusing uniforms and attributes leads to errors ([2b470d21406e42f](https://github.com/Zernikalos/Zernikalos/commit/2b470d21406e42f92e4119dfd68859732c64a64a))
- 🐛 Errors during WebGL rendering process ([867bb70aa853e90](https://github.com/Zernikalos/Zernikalos/commit/867bb70aa853e90169261f475b4c06a0066d319d))
- 🐛 Minor fixes ([bc1d4dfcc0673c3](https://github.com/Zernikalos/Zernikalos/commit/bc1d4dfcc0673c3fb8e35562ee1f1dadf8b6af30))
- 🐛 Minor fixes and cleanups on the refactor ([1e96516f0768af8](https://github.com/Zernikalos/Zernikalos/commit/1e96516f0768af8aaf115bf1e34a1b4adba83101))
- 🐛 Fixing problems with web sample ([cdbbf1180386b89](https://github.com/Zernikalos/Zernikalos/commit/cdbbf1180386b897220ba27373579d67b6e6ef7b))
- 🐛 Upgrading and fixing JVM target ([7ff3f6cd1d66109](https://github.com/Zernikalos/Zernikalos/commit/7ff3f6cd1d66109cab7f78d68f9166a5b1c697b6))
- 🐛 Fixing problems showing the models, bad computations ([eca771574f0eac4](https://github.com/Zernikalos/Zernikalos/commit/eca771574f0eac45cf01f9c7c7568cd426b7a778))
- Error related to indices in the attributes ([dc50069040daa37](https://github.com/Zernikalos/Zernikalos/commit/dc50069040daa37452c4aacbfbee3d21894fc473))
- 🐛 Small fix setting MrGroup as default serializer ([a977e13bb09ef7f](https://github.com/Zernikalos/Zernikalos/commit/a977e13bb09ef7f4418a23ebeebb134385cf9a49))
- 🐛 Fixing problem of not accessing to parent when computing ([71f771664a2a562](https://github.com/Zernikalos/Zernikalos/commit/71f771664a2a562d02597b64df626ef4fad60923))
- 🐛 Fixing error in compilation on the rendering context ([e6bd9190f5b14e8](https://github.com/Zernikalos/Zernikalos/commit/e6bd9190f5b14e8b55a6f33552b7ec2ce6ddb2b2))
- 🐛 Fixing android sample after scene context addition ([9b1fd34e53ded44](https://github.com/Zernikalos/Zernikalos/commit/9b1fd34e53ded446d41f34284efd8d9adc8de205))


### Changed

- 💡 Using uniform IDs from definition ([4980e23f18f36c5](https://github.com/Zernikalos/Zernikalos/commit/4980e23f18f36c5a52070076af5f3e1141dfd6fa))
- 💡 Removal of ZRefMaterial, using plain material instead ([5d47b9c80917777](https://github.com/Zernikalos/Zernikalos/commit/5d47b9c8091777708a5bfa710dd1f6dff8d61505))
- 💡 Improvements on the ZUniformBlock generation ([d7d06a991578424](https://github.com/Zernikalos/Zernikalos/commit/d7d06a9915784243b85d2152b018b50e07daf4a2))
- 💡 Improving current transformation into data classes for declarations ([f8400a9a19807af](https://github.com/Zernikalos/Zernikalos/commit/f8400a9a19807af06659dba87906dc723103b877))
- 💡 Refactoring to value + type management on uniforms ([01f3120d3e409e2](https://github.com/Zernikalos/Zernikalos/commit/01f3120d3e409e298769e9229d9729fede4ab8d8))
- 💡 First steps towards the refactoring ([d9d20f8ffd767ff](https://github.com/Zernikalos/Zernikalos/commit/d9d20f8ffd767ff3fc3ab1cfef505349344a2499))
- 💡 Removing renderer field from interfaces ([df54dbd9faa74de](https://github.com/Zernikalos/Zernikalos/commit/df54dbd9faa74ded9e247e50e724bb37a2a40872))
- 💡 RenderizableComponent simplification ([197371e50092b46](https://github.com/Zernikalos/Zernikalos/commit/197371e50092b467a1bafa7b7774cf61835f00c7))
- 💡 Removal of data dependency from BaseComponent ([4e3f3dcfc86b1e5](https://github.com/Zernikalos/Zernikalos/commit/4e3f3dcfc86b1e5e65bf77f2c54877b45fc7af5d))
- 💡 Renaming the class to ZRenderer for simplicity ([a5abf7eb79732c6](https://github.com/Zernikalos/Zernikalos/commit/a5abf7eb79732c6f0616b527b7c97b09478d4f64))
- 💡 Using just one ZRenderer interface ([ab352d4bb6d7c0a](https://github.com/Zernikalos/Zernikalos/commit/ab352d4bb6d7c0a03ab9cbce9219f8a2b9dd98bf))
- 💡 Removal of some refcomponents used around ([5a3247b10e486f3](https://github.com/Zernikalos/Zernikalos/commit/5a3247b10e486f33494795c950590eefdb450e46))
- 💡 Removal of serialization from Shader module ([d37553995db1ec4](https://github.com/Zernikalos/Zernikalos/commit/d37553995db1ec4c86ce9d26641c582acd67b1ae))
- 💡 Minor improvements on animation related classes ([88b8e2c77a387ce](https://github.com/Zernikalos/Zernikalos/commit/88b8e2c77a387ce28de6d88f233ab6931a747315))
- 💡 Reimplementing ZSurfaceViewHandler as a simple state machine for simplification ([e4f0832b5e83db3](https://github.com/Zernikalos/Zernikalos/commit/e4f0832b5e83db3bd50f96a3358a5f88a3e7bb15))
- 💡 Refactoring and documenting the ZSceneStateHandler interface ([781498aab8c9764](https://github.com/Zernikalos/Zernikalos/commit/781498aab8c976477176182fc1ab5da0c488f822))
- 💡 Internal classes renaming ([a2eb3872a005950](https://github.com/Zernikalos/Zernikalos/commit/a2eb3872a00595089f49fce58b07ae8eac75ed67))
- 💡 Change Objects into List instead of map ([cc30e17abfeaee7](https://github.com/Zernikalos/Zernikalos/commit/cc30e17abfeaee7bdf29f495a264fa6cb442eff3))
- 💡 Converting ZObjects in ZRef elements ([ea3ce6ad3a4e03b](https://github.com/Zernikalos/Zernikalos/commit/ea3ce6ad3a4e03b4c4ef893b80bf9a76bdc41db7))
- 💡 Using standard Uuid Kotlin class for refId in components ([7582ab838d0c418](https://github.com/Zernikalos/Zernikalos/commit/7582ab838d0c418565507fca088a6b29960320ce))
- 💡 Renaming values to floatArray in ZAlgebraObject ([057e8e5d93d139b](https://github.com/Zernikalos/Zernikalos/commit/057e8e5d93d139be3e2482a69206572b41895668))
- 💡 Improving how the Uniform classes behaves in a generic way ([1162333ccdc89f2](https://github.com/Zernikalos/Zernikalos/commit/1162333ccdc89f2c0def8469e5e6b1c968fd3e6a))
- 💡 Renaming to ZUniformBlock ([62856ce60974183](https://github.com/Zernikalos/Zernikalos/commit/62856ce6097418371cfcab1368f41d9f40c51f35))
- 💡 Improving data handling on UniformBuffers ([a5cf31ec709e026](https://github.com/Zernikalos/Zernikalos/commit/a5cf31ec709e0264c70ea89ed6b586181c4ca908))
- 💡 Renaming ZAttributesEnabler as ZShaderProgramParameters ([cfa9e802a2ef375](https://github.com/Zernikalos/Zernikalos/commit/cfa9e802a2ef375ccbe883c5bb6044ac79ff7060))
- 💡 Small refactor for ZShaderProgramParameters ([17b2801f6bb8674](https://github.com/Zernikalos/Zernikalos/commit/17b2801f6bb86747132f68e2513446ccfccea98b))
- 💡 Small improvements on ZComponent naming ([cf84c1cb56b0f7c](https://github.com/Zernikalos/Zernikalos/commit/cf84c1cb56b0f7c49a8c5e6e883ff41c17edbbcb))
- 💡 Migrating ShaderGenerator as well as enabler setup to its own class ([8c8824e5f4be494](https://github.com/Zernikalos/Zernikalos/commit/8c8824e5f4be494dbb6bf55c793e3319017ea83c))
- 💡 Name changing for ShaderSourceGenerator ([53ef4bdc698564f](https://github.com/Zernikalos/Zernikalos/commit/53ef4bdc698564f9ece88d697c4a0524b78809c9))
- 💡Improvements on ZTransform class format ([69845596a9a1c91](https://github.com/Zernikalos/Zernikalos/commit/69845596a9a1c91aeb7f37c73eca1a28b652f835))
- 💡Improvements on memory usage for Vec4 ([669bc3db23c6cc7](https://github.com/Zernikalos/Zernikalos/commit/669bc3db23c6cc7da810a25f7a1b0a0145b16ab0))
- 💡Improvements on memory usage for quaternions ([f9ade09d504dc37](https://github.com/Zernikalos/Zernikalos/commit/f9ade09d504dc37d9bd207e2857e7fc8357d2176))
- 💡Excluding skinning changes from js platform ([673129d8e810c79](https://github.com/Zernikalos/Zernikalos/commit/673129d8e810c7976e02e72f6dcf80bbf283f13a))
- 💡Skinning removal ([ffca2cb5a9d5ca0](https://github.com/Zernikalos/Zernikalos/commit/ffca2cb5a9d5ca02c6de856fc9e0ac8c9c82a9ea))
- 💡Changing location for position on ZTransform ([7066bc7d79159bc](https://github.com/Zernikalos/Zernikalos/commit/7066bc7d79159bcf16c112af735d015af6af1555))
- 💡Refactor texture rendering for platform-specific implementations ([eba692b45781f23](https://github.com/Zernikalos/Zernikalos/commit/eba692b45781f239abd05053e57f3c3e408d10f3))
- 💡 Simplification of components hierarchy ([f5363206086f072](https://github.com/Zernikalos/Zernikalos/commit/f5363206086f0723c91b5d5a2a2282218451bf92))
- 💡Improving the viewport resizing naming ([0f2bd664c7fda82](https://github.com/Zernikalos/Zernikalos/commit/0f2bd664c7fda8261618659cf39235f644e86144))
- 💡Convert ZBufferKey and ZRawBuffer into simple data classes ([6733d2e0660f778](https://github.com/Zernikalos/Zernikalos/commit/6733d2e0660f778e67119af3454a23f6a9d9ac27))
- 💡Removal ZBufferKey and ZRawBuffer as components ([bba5802948100ac](https://github.com/Zernikalos/Zernikalos/commit/bba5802948100ac26f7d641ea6f296d38c6079c5))
- 💡Convert ZLens into ZComponents ([36ebca2c9aef754](https://github.com/Zernikalos/Zernikalos/commit/36ebca2c9aef75436421f86e09a61bee0868d00c))
- 💡Improvements on the ZRefComponent ([b9cea9726167ab8](https://github.com/Zernikalos/Zernikalos/commit/b9cea9726167ab8729192df9d468d09e9d894861))
- 💡Removal of ZRef interface and replaced by ZRefComponent ([c7eed47a7d464ce](https://github.com/Zernikalos/Zernikalos/commit/c7eed47a7d464ce8ff468bd87b824ddbf7972ffa))
- 💡ZComponent simplification as an interface ([ab4032e56530657](https://github.com/Zernikalos/Zernikalos/commit/ab4032e565306579a45d4525f4b7c9c43990a452))
- 💡 Reusing current functions to reimplement search functions ([9864bc43ffae2f1](https://github.com/Zernikalos/Zernikalos/commit/9864bc43ffae2f10da0580e93c19d7d698c3f459))
- 💡 Improvements on the SearchIndex class ([433afb6dde9f897](https://github.com/Zernikalos/Zernikalos/commit/433afb6dde9f8971ed44d694fc4dce2035f847ed))
- 💡 Improving the uniform data pass ([97b8ab7ee9c99b5](https://github.com/Zernikalos/Zernikalos/commit/97b8ab7ee9c99b5a4fa9c4e868f7d286f0aab2f1))
- 💡 Renaming ZOsInfo to ZPlatformInfo ([fc343b0655ce065](https://github.com/Zernikalos/Zernikalos/commit/fc343b0655ce065a37f85cdb09ad3dadef4df501))
- 💡 Migrate code related to code generation into its own folder ([a887321b49892f4](https://github.com/Zernikalos/Zernikalos/commit/a887321b49892f486887577fa388a821a0a24168))
- 💡 Replace surface view event handler to its own file ([cc8b1f8c3bf08b7](https://github.com/Zernikalos/Zernikalos/commit/cc8b1f8c3bf08b70e12ee747a166e24971e006ce))
- 💡 Extend context handling on the engine ([b226942602efff9](https://github.com/Zernikalos/Zernikalos/commit/b226942602efff9aa82f49e76bf4d8d783bfb74d))
- 💡 Fixing some code warnings around ([6771be403733a0f](https://github.com/Zernikalos/Zernikalos/commit/6771be403733a0ff8366533c603a7fc0105d4007))
- 💡 Optimize import as well as adding Copyright statements ([996c30205962ec6](https://github.com/Zernikalos/Zernikalos/commit/996c30205962ec6cb16c40730d206aa8b587bb27))
- 💡 Small components refactoring ([75f2fdab2f1b83f](https://github.com/Zernikalos/Zernikalos/commit/75f2fdab2f1b83ffb4401ca3a44e1e5591f4cf7c))
- 💡 Removal the base component class and merge into component ([e7bc25fac742e73](https://github.com/Zernikalos/Zernikalos/commit/e7bc25fac742e7385579cb704acbd3edb8b39dac))
- 💡 Removal of ZProgram from common part ([7b8ff800eba8f3a](https://github.com/Zernikalos/Zernikalos/commit/7b8ff800eba8f3afe8d005ff850b42445988a9c2))
- 💡 Use again algebra objects as return type for uniform generators ([76182c1436ed8fc](https://github.com/Zernikalos/Zernikalos/commit/76182c1436ed8fcf74a433739a94b069454d90cf))
- 💡 UniforGenerator refactor for using values ([6b4117de22334b5](https://github.com/Zernikalos/Zernikalos/commit/6b4117de22334b56fe87debb3d823b42e97bf72b))
- 💡 Simplification of all component hierarchy ([fcaa5534354a962](https://github.com/Zernikalos/Zernikalos/commit/fcaa5534354a9629d7de3c2c1ffde72ab47b23c4))
- 💡 Moving all context related classes to its own package ([addff40211e2b43](https://github.com/Zernikalos/Zernikalos/commit/addff40211e2b4382e50a7e46f48f8cc1c5e6945))
- 💡 Changing folders for some buffer related classes ([b7268844a99de32](https://github.com/Zernikalos/Zernikalos/commit/b7268844a99de324dcda01d3d6c57afb113a2fc6))
- 💡 Removal of some OGL dependencies in common ([f10d4b57621be8d](https://github.com/Zernikalos/Zernikalos/commit/f10d4b57621be8d992558121b1c347315cd39e0a))
- 💡 Converting ZRenderingContext in Interface ([81381fc6694dcab](https://github.com/Zernikalos/Zernikalos/commit/81381fc6694dcab0ff2e74d3f245cfaca9bc9728))
- 💡 Moving renderers outside commonMain ([fcb20876b0acb48](https://github.com/Zernikalos/Zernikalos/commit/fcb20876b0acb4858174579a05c5e832861bfd17))
- 💡 Better exposing of class components attributes ([023078d2e4ac38d](https://github.com/Zernikalos/Zernikalos/commit/023078d2e4ac38dcaeeaadfe24594468ff638454))
- 💡 Splittin in Ctrl, Data, Render the components ([2e32d0d701f86c6](https://github.com/Zernikalos/Zernikalos/commit/2e32d0d701f86c6a3552285e0860b83484511f9c))
- 💡 Splitting components in init, bind, render ([6a0f4f222c41029](https://github.com/Zernikalos/Zernikalos/commit/6a0f4f222c410294a09860b6f3c5cc640f47b194))
- 💡 Refactoring and simplification of public API ([03dbb434ee9415a](https://github.com/Zernikalos/Zernikalos/commit/03dbb434ee9415a200f7911ebde07358589fb420))
- 💡 Renaming math classes, removing Float subfix ([50fe436ff9a6822](https://github.com/Zernikalos/Zernikalos/commit/50fe436ff9a6822bdf443187037ca328b235e92a))
- 💡 Minor changes to vector classes + removing support for JSON ([fed5b2b7189c5dc](https://github.com/Zernikalos/Zernikalos/commit/fed5b2b7189c5dc66fcc9c30e22e5ebd93375c35))
- 💡 Improvin proto format and class structure ([5a5f569554bbe26](https://github.com/Zernikalos/Zernikalos/commit/5a5f569554bbe26ab51e5df9108159eee0a167cc))
- 💡 Migrate to Z format ([3a1e9257d488b17](https://github.com/Zernikalos/Zernikalos/commit/3a1e9257d488b178804b4efabe71d58211370c83))
- 💡 Samples into its own repository ([e75010eb9e40839](https://github.com/Zernikalos/Zernikalos/commit/e75010eb9e40839385412897577bddd95d981f24))
- 💡 Exposing math objects to JS ([031dedb0693ffb5](https://github.com/Zernikalos/Zernikalos/commit/031dedb0693ffb5e9e056e1176bdfc0d7fb109ff))
- 💡 Improving the state handler for context passing ([05802d2909667f4](https://github.com/Zernikalos/Zernikalos/commit/05802d2909667f4370d3f6b3c830f503da99a3a7))
- 💡 Moving scene context location ([78f190f236cf48f](https://github.com/Zernikalos/Zernikalos/commit/78f190f236cf48f975cf21977af456f20e11cff6))
- 💡 Passing rendering context through statehandler ([bdc7337d4704757](https://github.com/Zernikalos/Zernikalos/commit/bdc7337d470475724cd641dda6a540db2e60916c))
- 💡 IoD with context in the components ([8abc60f55d2795c](https://github.com/Zernikalos/Zernikalos/commit/8abc60f55d2795cd8b5f942fac2681bea29d17a2))
- Improving the current public API at least for JS ([a662acb7bcf74eb](https://github.com/Zernikalos/Zernikalos/commit/a662acb7bcf74eb6b351794ca02660712e3be890))
- 💡 Removal of ugly custom serializers ([9a6a63a399d24dd](https://github.com/Zernikalos/Zernikalos/commit/9a6a63a399d24ddeff9f3ad4ea820c8f24b6f5a2))
- 💡 Improving code visibility properties ([eaf02d61b8d95a2](https://github.com/Zernikalos/Zernikalos/commit/eaf02d61b8d95a271de09cba4a33a428bf2fefe0))

### Documentation

- ✏️ Setting Copyright note on webgpu files ([49c891bacdf769e](https://github.com/Zernikalos/Zernikalos/commit/49c891bacdf769e473afbb03897640d13b6a04dc))
- ✏️ Added documentation for ZComponent.kt file ([9409c840411260a](https://github.com/Zernikalos/Zernikalos/commit/9409c840411260ad01a073d7850b37a6f05174b0))
- ✏️ Improvements on documentation generation ([8b74e7248be7406](https://github.com/Zernikalos/Zernikalos/commit/8b74e7248be7406f9535b163ca9f33aee0726a4e))
- ✏️ Adding docs for ZMatrix4 ([b72a2c5a3e53921](https://github.com/Zernikalos/Zernikalos/commit/b72a2c5a3e5392141b1d25ee73d5733940f5fea8))
- ✏️ Adding documentation for ZTexture class ([74b1d7f6c4bc0a4](https://github.com/Zernikalos/Zernikalos/commit/74b1d7f6c4bc0a40c96ba25aa1a02bca24346463))
- ✏️ Fix small url issue in README.md ([eb426b6784ac9b7](https://github.com/Zernikalos/Zernikalos/commit/eb426b6784ac9b7cd87bd735687c57a47b24ea33))
- ✏️ Adding documentation to classes ([0fd970305a7ac7f](https://github.com/Zernikalos/Zernikalos/commit/0fd970305a7ac7f190ea517c9a0480401b752137))
- ✏️ Enhance documentation and update dokka config ([bda05a837deaadb](https://github.com/Zernikalos/Zernikalos/commit/bda05a837deaadbeaa88229f2dc3fffb4a5244d1))
- ✏️ Add Dokka for documentation generation ([ed15157ec1e19e4](https://github.com/Zernikalos/Zernikalos/commit/ed15157ec1e19e43d4847ea6da935c419dc54821))
- ✏️ Add README file ([f123f0b5be911a7](https://github.com/Zernikalos/Zernikalos/commit/f123f0b5be911a7ba6ed7d30604792a82809bcd3))

### Style

- 💄 Minor changes done to the code + Licensing ([3200d6604da9539](https://github.com/Zernikalos/Zernikalos/commit/3200d6604da953983b2a113440b760bbca5a750c))




### Chore

- 🤖 Improve release process documentation ([5ac43985751f4ff](https://github.com/Zernikalos/Zernikalos/commit/5ac43985751f4ff96234023c0629884af74b7de7))
- 🤖 Fast build CI added ([301dfac8570debc](https://github.com/Zernikalos/Zernikalos/commit/301dfac8570debc13d9d58bc19c5fa9012cb09e9))
- 🤖 Splitting CI in build and release ([5f29e81d0a74c31](https://github.com/Zernikalos/Zernikalos/commit/5f29e81d0a74c31611c85ba89aa079e9387e2561))
- 🤖 CI fix ([ba4b1b4f4a9e47b](https://github.com/Zernikalos/Zernikalos/commit/ba4b1b4f4a9e47b798c616853880e34aecc095f1))
- 🤖 Migration to GitHub packages + actions ([63e4474a7c3060a](https://github.com/Zernikalos/Zernikalos/commit/63e4474a7c3060a894b8d01279ffe5698e5aa12c))
- 🤖 Dependency upgrade ([eb6fab7577716b9](https://github.com/Zernikalos/Zernikalos/commit/eb6fab7577716b9df03ee2d1b9991d8c7fefb56a))
- 🤖 Improvements on the version upgrading ([7e56848f45ad000](https://github.com/Zernikalos/Zernikalos/commit/7e56848f45ad0002dbad62180ef5d64e16f3981b))
- 🤖 Improving repository config ([cbb86192c756c68](https://github.com/Zernikalos/Zernikalos/commit/cbb86192c756c68fd6c17651340d3ca404a8668e))
- 🤖 Upgrade versions ([1dae8e9d9ffdfee](https://github.com/Zernikalos/Zernikalos/commit/1dae8e9d9ffdfee8c013bc43619c3e16943c52d6))
- 🤖 Version upgrading through VERSION.txt file ([f1b36634c71bd9e](https://github.com/Zernikalos/Zernikalos/commit/f1b36634c71bd9ee86550f8bc52c329e8a8194c7))
- 🤖 Improve editor configuration ([e0ec9d42e0acf76](https://github.com/Zernikalos/Zernikalos/commit/e0ec9d42e0acf762f4960caaef54468540497331))
- 🤖 Adding basic windsurf ide templates ([9d50d56d1200b6e](https://github.com/Zernikalos/Zernikalos/commit/9d50d56d1200b6e9a40ae535180d79827b61f9a0))
- 🤖 Upgrade dependencies ([43c9acb11ee3459](https://github.com/Zernikalos/Zernikalos/commit/43c9acb11ee3459207a02f52185efdff095edb76))
- 🤖 Upgrade Gitlab CI ([cb127d649bf0296](https://github.com/Zernikalos/Zernikalos/commit/cb127d649bf02966b54db4de39317d9899a18e1a))
- 🤖 Upgrade Zernikalos and Kotlin version ([c421133db2886a7](https://github.com/Zernikalos/Zernikalos/commit/c421133db2886a7b06d7f77623a703ee8a238775))
- 🤖 Setup gitlab CI ([f23e2323a6a05b8](https://github.com/Zernikalos/Zernikalos/commit/f23e2323a6a05b825c886fc3cb2fa46cb2d1603c))
- 🤖 Dokka version upgrade ([eb88d465f6563ad](https://github.com/Zernikalos/Zernikalos/commit/eb88d465f6563ad7fd3c1a92280524101cf7fda6))
- 🤖 Dependency upgrade ([9427b2691f0761e](https://github.com/Zernikalos/Zernikalos/commit/9427b2691f0761e1071e6821089ac15e8384a1eb))
- 🤖 Upgrade gradle version an dependencies ([14dd42f7395226c](https://github.com/Zernikalos/Zernikalos/commit/14dd42f7395226ce59bba4dc0bfc940f2f1c3a91))
- 🤖 Improving gradle config and version management ([9c334dc73f4cfff](https://github.com/Zernikalos/Zernikalos/commit/9c334dc73f4cfff56d0a4ff082c69267616dea9f))
- 🤖 Upgrade kotlin version ([30a9c504406d4f0](https://github.com/Zernikalos/Zernikalos/commit/30a9c504406d4f01337c8a0c9ed3fe9a1de59e11))
- 🤖 Update Zernikalos base url ([460b0d2291b7f8b](https://github.com/Zernikalos/Zernikalos/commit/460b0d2291b7f8b680165a72d62e0071c764991a))
- 🤖 Small improvements to the build scripts ([9392cd8e3902878](https://github.com/Zernikalos/Zernikalos/commit/9392cd8e39028788e501e48877f7aec4c5aa4f9a))
- 🤖Add support for publishing Zernikalos ([888261c9a8c1dc1](https://github.com/Zernikalos/Zernikalos/commit/888261c9a8c1dc16911619b067febff8221bc713))
- 🤖 Making the JS build compatible with zdebugger ([b12d307fa9caaab](https://github.com/Zernikalos/Zernikalos/commit/b12d307fa9caaab5e1c4ff3be166948c47d50dbd))
- 🤖 Upgrade kotlin version ([59004fef95de61a](https://github.com/Zernikalos/Zernikalos/commit/59004fef95de61a001f2fe5ce89d8059416b3290))
- 🤖 Kotlin version upgrade ([eb5b801f534cc22](https://github.com/Zernikalos/Zernikalos/commit/eb5b801f534cc22ef33a44dacae6fbc4be4b5cc2))
- 🤖 Kotlin version upgrade ([b3e8a458c881b48](https://github.com/Zernikalos/Zernikalos/commit/b3e8a458c881b48aa2c7fb22f2dfd08ab4e25c80))
- 🤖 Upgrade kotlin and gradle version ([cad14f3711426e5](https://github.com/Zernikalos/Zernikalos/commit/cad14f3711426e53d7f8d69c9f11925d7f89f1fb))
- 🤖 Support for Apple Projects build ([166578954c530d6](https://github.com/Zernikalos/Zernikalos/commit/166578954c530d6b65fa36e0b36412e0e3f6fd4e))
- 🤖 Upgrading gitignore ([12db6c8b10b82fc](https://github.com/Zernikalos/Zernikalos/commit/12db6c8b10b82fcec2588598f19843e505ae59ed))
- 🤖 Fixing gitignore ([840f356b6c9227c](https://github.com/Zernikalos/Zernikalos/commit/840f356b6c9227cb59f773700814d020b551599e))
- 🤖 Upgrade and remove some old dependencies ([b1d3e259b00c226](https://github.com/Zernikalos/Zernikalos/commit/b1d3e259b00c2262fe9afbf4658258dd48e3176a))
- 🤖 Fixing config by upgrading to gradle 8 ([2a6fc1b430da9d3](https://github.com/Zernikalos/Zernikalos/commit/2a6fc1b430da9d39fd5d742cdfa67e9c6e2d10f1))
- 🤖 Upgrading the Kotlin version to 1.8.10 ([cc5f7a3dc83bc25](https://github.com/Zernikalos/Zernikalos/commit/cc5f7a3dc83bc25b6334ca9b2fa8e444faffb953))
- 🤖 Adding basic linter configuration ([650b7dd8dbfd96c](https://github.com/Zernikalos/Zernikalos/commit/650b7dd8dbfd96c5c63503c0fb91c390b5494fdc))
- 🤖 Improve gradle version set up ([39db09691e28c4a](https://github.com/Zernikalos/Zernikalos/commit/39db09691e28c4a08214cecba84045974d90013b))
- 🤖 Upgrade to kotlin 1.8 ([ac240109ae370db](https://github.com/Zernikalos/Zernikalos/commit/ac240109ae370db053f6e111f433828685709bd3))
- 🤖 Rolling back changes in project structure ([0636a8546bfe7d1](https://github.com/Zernikalos/Zernikalos/commit/0636a8546bfe7d1c23c72055ea653ba097411b3b))
- 🤖 Upgrade IDEA project format and structure ([4dd7970a6ef354c](https://github.com/Zernikalos/Zernikalos/commit/4dd7970a6ef354c7730200f2a101275aeeeb8982))
- 🤖 Remove of annoying warnings due to experimental flags ([e7d7b030d90dc70](https://github.com/Zernikalos/Zernikalos/commit/e7d7b030d90dc70b7f9f1990276b416c30488267))

