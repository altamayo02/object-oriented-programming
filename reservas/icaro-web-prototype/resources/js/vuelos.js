export default class Vuelos {
    static #table
    static #modal
    static #currentOption // add | edit | delete
    static #formVuelos
    static #trayectos
    static #aviones

    constructor() {
        throw new Error('Esta clase no permite el uso del constructor. Utilice Personal.init()')
    }

    static async init() {
        const response = await Helpers.fetchData(`${urlAPI}/vuelos`)
        // :: Ejemplo de aplanado de objetos, ver Helpers.flatten() y Helpers.flat()
        //const data = Helpers.flat(response.data)
        const data = response.data

        Vuelos.#formVuelos = await Helpers.loadPage('./resources/html/vuelo.html')

        // leer los trayectos y concatener origen y destino en cada objeto
        Vuelos.#trayectos = await Helpers.fetchData(`${urlAPI}/trayectos`)
        Vuelos.#trayectos.data.forEach(t => (t.toString = `${t.origen} – ${t.destino} ($${t.costo})`))

        // leer los aviones y concatenar la matrícula con el modelo
        Vuelos.#aviones = await Helpers.fetchData(`${urlAPI}/aviones`)
        Vuelos.#aviones.data.forEach(a => (a.toString = `${a.matricula} – ${a.modelo}`))

        document.querySelector('main').innerHTML = `
        <div class="p-2 w-full">
            <div id="vuelos" class="m-2"></div>
        </dv>
    `
        Vuelos.#table = new Tabulator('#vuelos', {
            height: 'calc(100vh - 160px)', // establecer la altura de la tabla, esto habilita el DOM virtual y mejora drásticamente la velocidad de procesamiento
            data: data, // asignar los datos a la tabla
            layout: 'fitColumns', // ajustar columnas al ancho de la tabla (opcional)
            columns: [
                // definir las columnas de la tabla
                { title: 'ORIGEN', field: 'trayecto.origen', width: 150 },
                { title: 'DESTINO', field: 'trayecto.destino', hozAlign: 'left' },
                { title: 'AVIÓN', field: 'avion.matricula', hozAlign: 'left' },
                {
                    title: 'FECHA/HORA',
                    field: 'fechaHora',
                    hozAlign: 'center',
                    formatter: 'datetime',
                    formatterParams: {
                        inputFormat: 'iso',
                        outputFormat: 'yyyy-MM-dd hh:mm a',
                        invalidPlaceholder: 'Fecha y hora inválidas'
                    }
                },
                {
                    title: 'DURACIÓN',
                    field: 'trayecto.duracion',
                    hozAlign: 'center',
                    formatter: cell => Duration.fromISO(cell.getValue()).toFormat('hh:mm')
                },
                { title: 'COSTO', field: 'trayecto.costo', hozAlign: 'center', formatter: 'money' },
                { title: 'ESTADO', field: 'estado', hozAlign: 'center' },
                { formatter: Vuelos.#editRowButton, width: 40, hozAlign: 'center', cellClick: Vuelos.#editRowClick },
                { formatter: Vuelos.#deleteRowButton, width: 40, hozAlign: 'center', cellClick: Vuelos.#deleteRowClick }
            ],
            footerElement: `
                <div class='container-fluid d-flex justify-content-end p-0'>
                    <button id='add-vuelos' class='btn btn-info btn-sm'>${icons.plusSquare}&nbsp;&nbsp;Nuevo vuelo</button>
                </div>
            `.trim()
        })

        // observe que toma automáticamente el ID de cada objeto del array, esto no sucederá con la identificación
        // de los usuarios. Habrá que mapear el array de usuarios y agregar el ID con el mismo valor de la identificación
        Vuelos.#table.on('rowClick', (e, row) => console.log('Clic sobre la fila con ID: ' + row.getData().id))
        Vuelos.#table.on('tableBuilt', () => document.querySelector('#add-vuelos').addEventListener('click', Vuelos.#addRow))
    }

    static #editRowButton = (cell, formatterParams, onRendered) => `
        <button id="edit-row" class="border-0 bg-transparent" data-bs-toggle="tooltip" title="Editar">${icons.editMediumPurple}</button>
    `

    static #editRowClick = (e, cell) => {
        Vuelos.#currentOption = 'edit'
        Vuelos.#modal = new Modal({
            size: 'default',
            title: 'Actualizar un vuelo',
            content: Vuelos.#formVuelos,
            buttons: [
                {
                    id: 'edit-flights',
                    style: 'btn btn-primary',
                    html: `${icons.editWhite}&nbsp;&nbsp;<span>Actualizar</span>`,
                    callBack: () => Vuelos.#edit(cell)
                },
                {
                    id: 'cancel-add-flights',
                    style: 'btn btn-secondary',
                    html: `${icons.xLg}<span>Cancelar</span>`,
                    callBack: () => Vuelos.#modal.dispose()
                }
            ],
            built: idModal => Vuelos.#toComplete(idModal, cell.getRow().getData())
        }).show()
    }

    static async #edit(cell) {
        // verificar si los datos cumplen con las restricciones indicadas en el formulario HTML
        if (!Helpers.okForm('#form-vuelos')) {
            return
        }

        const data = Vuelos.#getFormData()
        console.log(data)

        try {
            // enviar la solicitud de actualización con los datos del formulario
            let response = await Helpers.fetchData(`${urlAPI}/vuelos/${cell.getRow().getData().id}`, {
                method: 'PUT',
                body: data
            })

            if (response.message === 'ok') {
                Toast.info({ message: 'Vuelo actualizado exitosamente' })
                cell.getRow().update(response.data)
                Vuelos.#modal.dispose()
            } else {
                Toast.info({ message: 'No se pudo actualizar el vuelo', mode: 'danger', error: response })
            }
        } catch (e) {
            Toast.info({ message: 'Sin acceso a la actualización de vuelos', mode: 'danger', error: e })
        }
    }

    static #deleteRowButton = (cell, formatterParams, onRendered) => `
        <button id="delete-row" class="border-0 bg-transparent" data-bs-toggle="tooltip" title="Eliminar">${icons.delete}</button>
    `

    static #deleteRowClick = (e, cell) => {
        Vuelos.#currentOption = 'delete'
        ;(Vuelos.#modal = new Modal({
            title: 'Eliminar un vuelo',
            content: `
                Confirme la eliminación del vuelo:<br>
                ${cell.getRow().getData().trayecto.origen} – ${cell.getRow().getData().trayecto.destino}<br>
                Fecha y hora: ${DateTime.fromISO(cell.getRow().getData().fechaHora).toFormat('yyyy-MM-dd hh:mm a')}<br>
            `,
            buttons: [
                {
                    id: 'add-flights',
                    style: 'btn btn-danger',
                    html: `${icons.deleteWhite}<span>Eliminar</span>`,
                    callBack: () => Vuelos.#delete(cell)
                },
                {
                    id: 'cancel-add-flights',
                    style: 'btn btn-secundary',
                    html: `${icons.xLg}<span>Cancelar</span>`,
                    callBack: () => Vuelos.#modal.dispose()
                }
            ]
        })).show()
    }

    static async #delete(cell) {
        const id = cell.getRow().getData().id

        try {
            // enviar la solicitud de eliminación
            let response = await Helpers.fetchData(`${urlAPI}/vuelos/${id}`, {
                method: 'DELETE'
            })

            if (response.message === 'ok') {
                Toast.info({ message: 'Vuelo eliminado exitosamente' })
                cell.getRow().delete()
                this.#modal.dispose()
            } else {
                Toast.info({ message: 'No se pudo eliminar el vuelo', mode: 'danger', error: response })
            }
        } catch (e) {
            Toast.info({ message: 'Sin acceso a la eliminación de vuelos', mode: 'danger', error: e })
        }
    }

    static #addRow = async e => {
        Vuelos.#currentOption = 'add'
        Vuelos.#modal = new Modal({
            size: 'default',
            title: 'Agregar un vuelo',
            content: Vuelos.#formVuelos,
            buttons: [
                {
                    id: 'add-flights',
                    style: 'btn btn-primary',
                    html: `${icons.plusSquare}&nbsp;&nbsp;<span>Agregar</span>`,
                    callBack: Vuelos.#add
                },
                {
                    id: 'cancel-add-flights',
                    style: 'btn btn-secondary',
                    html: `${icons.xLg}<span>Cancelar</span>`,
                    callBack: () => Vuelos.#modal.dispose()
                }
            ],
            built: Vuelos.#toComplete
        }).show()
    }

    static async #add() {
        // verificar si los datos cumplen con las restricciones indicadas en el formulario HTML
        if (!Helpers.okForm('#form-vuelos')) {
            return
        }

        const data = Vuelos.#getFormData()

        try {
            // enviar la solicitud de creación con los datos del formulario
            let response = await Helpers.fetchData(`${urlAPI}/vuelos`, {
                method: 'POST',
                body: data
            })

            if (response.message === 'ok') {
                Vuelos.#table.addRow(response.data)
                Vuelos.#modal.dispose()
                Toast.info({ message: 'Vuelo agregado exitosamente' })
            } else {
                Toast.info({ message: 'No se pudo agregar el vuelo', mode: 'danger', error: response })
            }
        } catch (e) {
            Toast.info({ message: 'Sin acceso a la creación de vuelos', mode: 'danger', error: e })
        }
    }

    static #toComplete(idModal, rowData) {
        // crear la lista de opciones para el select de trayectos
        const trayectos = Helpers.toOptionList({
            items: Vuelos.#trayectos.data,
            value: 'id',
            text: 'toString',
            selected: Vuelos.#currentOption === 'edit' ? rowData.trayecto.id : ''
        })

        document.querySelector(`#${idModal} #trayectos`).innerHTML = trayectos

        // crear la lista de opciones para el select de aviones
        const aviones = Helpers.toOptionList({
            items: Vuelos.#aviones.data,
            value: 'matricula',
            text: 'toString',
            selected: Vuelos.#currentOption === 'edit' ? rowData.avion.matricula : ''
        })

        document.querySelector(`#${idModal} #aviones`).innerHTML = aviones

        if (Vuelos.#currentOption === 'add') {
            document.querySelector(`#${idModal} #estados`).disabled = true
        } else {
            document.querySelector(`#${idModal} #fecha-hora`).value = rowData.fechaHora
        }
    }

    /**
     * Recupera los datos del formulario y crea un objeto para ser retornado
     * @returns Un objeto con los datos del vuelo
     */
    static #getFormData() {
        const fechaHora = document.querySelector(`#${Vuelos.#modal.id} #fecha-hora`).value
        const idTrayecto = document.querySelector(`#${Vuelos.#modal.id} #trayectos`).value
        const matriculaAvion = document.querySelector(`#${Vuelos.#modal.id} #aviones`).value
        const estado = document.querySelector(`#${Vuelos.#modal.id} #estados`).value

        return { fechaHora, idTrayecto, matriculaAvion, estado }
    }
}
