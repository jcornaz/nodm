# NODM 
[![License](https://img.shields.io/badge/license-LGPL--3.0-blue.svg)](LICENSE)
[![Project status](https://img.shields.io/badge/status-discontinued-lightgray.svg)](https://gist.githubusercontent.com/jcornaz/46736c3d1f21b4c929bd97549b7406b2/raw/ProjectStatusFlow)

Notes Object Document Mapping. This project is an ORM-like library which makes possible to map Kotlin/Java objects to Lotus Notes documents.

## Project status
This project is discontinued, and stay here for consultation purpose. The project won't be maintened and no support will be provided.

Everyone is free to fork and create derivated products.

## Setup
This project need a `Notes.jar` archive, that is property of IBM and cannot be distributed. Therefore, you need a licensed installation of Lotus Notes.

1. Start by cloning this repo : `git clone --branch v0.1.1 git@github.com:jcornaz/notes.stub.git`
2. Create a folder "libs" at the root of the project and put there a copy the `Notes.jar` from your Lotus Notes installation.
3. Test it : `./gradlew check`

From here you can work in the project folder or add it in your project with your favorite method.
