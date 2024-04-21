export default class Manager {
    static #name // el nombre de la clase manejada
    static #currOption // add | edit | delete según la elección del usuario
    static #table // la instancia de tipo Tabulator
    static #form // el código html del formulario que se inyecta en el modal
    static #modal // la ventana modal para agregar, editar y eliminar

    constructor() {
        if (this.constructor == Manager) {
            throw new Error('Manager class is abstract. It cannot be instantiated.')
        }
        throw new Error('Esta clase no permite el uso de un constructor. Utilice init() en vez.')
    }

    static async init() {
		const response = await Helpers.fetchData(`${urlAPI}/${Manager.#name}`)
		const data = response.data

		// cargar el código fuente del formulario para el ingreso y edición de vuelos
		Manager.#form = await Helpers.loadPage(`./resources/html/form-${Manager.#name}.html`)

		document.querySelector('main').innerHTML = `
			<div class="p-2 w-full">
				<div id="${Manager.#name}" class="m-2"></div>
			</dv>
		`

		// Manager.#table.on('rowClick', (e, row) => console.log(`Clicked over row: ${row.getData().id}`))
        // FIXME - Watch this out
		Manager.#table.on('tableBuilt', () => document.querySelector(`#add-${Manager.#name}`).addEventListener('click', Manager.#addRow))

        Manager.#initAttributes()
	}

    static #initAttributes()

    static #customFormatter(type) {
        let formatter
        switch (type) {
            case 'duration':
                formatter = (cell) => Duration.fromISO(cell.getValue()).toFormat('hh:mm')
                break;
        
            case 'editButton':
                formatter = (cell, formatterParams, onRendered) => `
                    <button id="edit-row" class="border-0 bg-transparent" data-bs-toggle="tooltip" title="Editar">
                        ${icons.editMediumPurple}
                    </button>
                `
                break;
        
            case 'deleteButton':
                formatter = (cell, formatterParams, onRendered) => `
                    <button id="delete-row" class="border-0 bg-transparent" data-bs-toggle="tooltip" title="Eliminar">
                        ${icons.delete}
                    </button>
                `
                break;
        
            default:
                formatter = () => {}
                break;
        }

        return formatter
    }

    /**
	 * Despliega el cuadro de diálogo que permite agregar registros
	 */
	/* static #addRow = () => {
		Manager.#currOption = 'add'
		Manager.#modal = new Modal({
			height: 'calc(100vh - 150px)', // establecer la altura de la tabla, esto habilita el DOM virtual y mejora drásticamente la velocidad de procesamiento
			title: `Añadir ${Manager.#name}`,
            content: Manager.#form,
            buttons: [
                {
                    id: 'add-flights',
                    style: 'btn btn-primary',
                    html: `${icons.plusSquare}<span> Añadir</span>`,
                    callBack: Manager.#add
                },
                {
                    id: 'cancel',
                    style: 'btn btn-secondary',
                    html: `${icons.xLg}<span> Cancelar</span>`,
                    callBack: () => Manager.#modal.dispose()
                }
            ],
			built: Manager.#toComplete
		}).show()
	} */


}