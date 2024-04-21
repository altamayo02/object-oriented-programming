# Cómo se importaron estas utilidades

Enseguida se describe cómo se importaron las utilidades de terceros necesarias para el proyecto:

## Bootstrap
Se ingresó a [Bootstrap 5.2.3 > Compiled CSS and JS](https://getbootstrap.com/docs/5.2/getting-started/download/#compiled-css-and-js) y  se pulsó sobre el botón [descargar](https://github.com/twbs/bootstrap/releases/download/v5.2.3/bootstrap-5.2.3-dist.zip).

Luego de descomprimir el archivo descargado, se copiaron los archivos *bootstrap.min.js*  y *bootstrap.min.js.map* en *./resources/utils/bootstrap*.

## Bootswatch
Se ingresó a [Bootswatch](https://bootswatch.com/) par dar una mirada a los temas alternivos a los oficiales de Bootstrap. Seguidamente, en la misma página, se eligió [GitHub](https://github.com/thomaspark/bootswatch/) > Code > [Download ZIP](https://github.com/thomaspark/bootswatch/archive/refs/heads/v5.zip).

Luego de descomprimir el archivo descargado, se copió la carpeta *dist* en *./resources/utils/* y se cambió el nombre *dist* por *bootswatch*.

## Popper
Se ingresó a [@popperjs/core CDN files](https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.6/dist/umd/), se pusó clic derecho sobre *popper.min.js* se eligió la opción *guardar enlace como* y finalmente se eligió la carpeta *./resources/utils/popper*. Igual procedimiento de descarga se hizo con el archivo *popper.min.js.map*.

## Tabulator
De la página de [Tabulator](https://tabulator.info/), se eligió en la parte superior derecha el icono de [GitHub](https://github.com/olifolkerd/tabulator) > Code > [Download ZIP](https://github.com/olifolkerd/tabulator/archive/refs/heads/master.zip).

Luego de descomprimir el archivo descargado, se copió la carpeta *dist* en *./resources/utils/* y se cambió el nombre *dist* por *tabulator*.

## Luxon
De la página de [Luxon](https://moment.github.io/luxon/#/), se eligió [Get Started](https://moment.github.io/luxon/#/?id=luxon) > [Install Guide](https://moment.github.io/luxon/#/install) > [ES6](https://moment.github.io/luxon/#/install?id=es6) y luego se dio clic derecho en [Download minified](https://moment.github.io/luxon/es6/luxon.min.js) > *Guardar enlace como*. Esta acción abre un cuadro de diálogo que permite guardar el archivo *luxon.min.js* en la carpeta *./resources/utils/luxon/* 

## Librería propia
Los archivos JS que se encuentran en la carpeta *own*, corresponden a utilidades creadas por Carlos Cuesta Iglesias, algunas veces tomando como base otras fuentes que son referenciadas en el código.
