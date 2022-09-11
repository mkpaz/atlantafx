---
title: Build
nav_order: 2
---

## Project structure

![Project structure]({{ site.baseurl }}{% link /assets/images/project-structure.png %})

| Directory    | Description                             |
|--------------|-----------------------------------------|
| .github      | GitHub Actions workflows                |
| base         | Additional Controls and Java API        |
| docs         | GitHub Pages project website            |
| sampler      | Sampler application                     |
| styles       | Theme sources (SASS)                    |

## Instructions

To build and run the whole project, including packaged Sampler image:

```sh
mvn install
mvn javafx:run -pl sampler
```

If you want to use hot reload (update CSS without restarting the Sampler app) you have to start app in development mode:

```sh
# start watching for SASS source code changes
mvn compile -pl styles -Pdev

# run sampler in dev mode
mvn javafx:run -pl sampler -Pdev
```

You can also build each Maven module individually:

```sh
mvn install -N
mvn install -pl styles
mvn install -pl base
mvn javafx:run -pl sampler
```
