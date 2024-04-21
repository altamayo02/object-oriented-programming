export default class Trayectos {
	static #table
	static #formTrayectos
	static #modal
	static #currOption

	constructor() {
		throw new Error('Esta clase no permite el uso del constructor. Utilice Personal.init()')
	}

	static async init() {
		const response = await Helpers.fetchData(`${urlAPI}/trayectos`)
		const data = response.data

		document.querySelector('main').innerHTML = `
			<div class="p-2 w-full">
				<div id="trayectos" class="m-2"></div>
			</dv>
		`

		Trayectos.#formTrayectos = await Helpers.loadPage('./resources/html/form-trayectos.html')

		Trayectos.#table = new Tabulator('#trayectos', {
			height: 'calc(100vh - 150px)', // establecer la altura de la tabla, esto habilita el DOM virtual y mejora drásticamente la velocidad de procesamiento
			data: data, // asignar los datos a la tabla
			layout: 'fitColumns', // ajustar columnas al ancho de la tabla (opcional)
			columns: [
				// definir las columnas de la tabla
				{ title: 'ORIGEN', field: 'origen', hozAlign: 'left' },
				{ title: 'DESTINO', field: 'destino', hozAlign: 'left' },
				{ title: 'DURACIÓN', field: 'duracion', hozAlign: 'center',
					formatter: (cell) => Duration.fromISO(cell.getValue()).toFormat('hh:mm')
				},
				{ title: 'COSTO', field: 'costo', hozAlign: 'center', formatter: 'money' },
				{ formatter: Trayectos.#editRowButton, width: 40, hozAlign: 'center', cellClick: Trayectos.#editRowClick },
				{ formatter: Trayectos.#deleteRowButton, width: 40, hozAlign: 'center', cellClick: Trayectos.#deleteRowClick },
			],
			footerElement: `
				<div class='container-fluid d-flex justify-content-end p-0'>
					<button id='add-trayectos' class='btn btn-info btn-sm'>     
						${icons.plusSquare}&nbsp;&nbsp;Nuevo trayecto
					</button>
				</div>
			`.trim()
		})

		// observe que toma automáticamente el ID de cada objeto del array, esto no sucederá con la identificación
		// de los trayectos. Habrá que mapear el array de trayectos y agregar el ID con el mismo valor de la identificación
		Trayectos.#table.on('rowClick', (e, row) => console.log('Clic sobre la fila con ID: ' + row.getData().id))
		// Asigna una respuesta addRow() al evento click
		Trayectos.#table.on('tableBuilt', () => document.querySelector('#add-trayectos').addEventListener('click', Trayectos.#addRow))
	}

	/**
	 * Despliega el cuadro de diálogo que permite agregar registros
	 */
	static #addRow = () => {
		Trayectos.#currOption = 'add'
		Trayectos.#modal = new Modal({
			title: 'Añadir Trayectos',
            content: Trayectos.#formTrayectos,
            buttons: [
                {
                    id: 'add-users',
                    style: 'btn btn-primary',
                    html: `${icons.plusSquare}<span> Añadir</span>`,
                    callBack: Trayectos.#add
                },
                {
                    id: 'cancel',
                    style: 'btn btn-secondary',
                    html: `${icons.xLg}<span> Cancelar</span>`,
                    callBack: () => Trayectos.#modal.dispose()
                }
            ],
			built: Trayectos.#toComplete
		}).show()
	}

	static async #add() {
		// forzar la validación de los datos antes de enviarlos al servidor
		if (!Helpers.okForm('#form-trayectos')) {
			return
		}
	
		let data = Trayectos.#getFormData()
	
		try {
			// enviar la solicitud de creación con los datos del formulario
			let response = await Helpers.fetchData(`${urlAPI}/trayectos`, {
				method: 'POST',
				body: data
			})
	
			if (response.message === 'ok') {
				Trayectos.#table.addRow(response.data)
				Trayectos.#modal.dispose()
				Toast.info({ message: 'Trayecto agregado exitosamente' })
			} else {
				Toast.info(
				   { message: 'No se agregó el trayecto', mode: 'danger', error: response }
				)
			}
		} catch (e) {
			Toast.info(
			   { message: 'Sin acceso a la creación de trayectos', mode: 'danger', error: e }
			)
		}
	}

    static #editRowButton = (cell, formatterParams, onRendered) => `
        <button id="edit-row" class="border-0 bg-transparent" data-bs-toggle="tooltip" title="Editar">
            ${icons.editMediumPurple}
        </button>
    `

    static #editRowClick = (e, cell) => {
        Trayectos.#currOption = 'edit'
		Trayectos.#modal = new Modal({
			size: 'default',
			title: 'Actualizar Trayectos',
			content: Trayectos.#formTrayectos,
			buttons: [
				{
					id: 'edit-flights',
					style: 'btn btn-primary',
					html: `${icons.editWhite}&nbsp;&nbsp;<span>Actualizar</span>`,
					callBack: () => Trayectos.#edit(cell)
				},
				{
					id: 'cancel',
					style: 'btn btn-secondary',
					html: `${icons.xLg}<span>Cancelar</span>`,
					callBack: () => Trayectos.#modal.dispose()
				}
			],
			built: idModal => Trayectos.#toComplete(idModal, cell.getRow().getData())
		}).show()
    }

	static async #edit(cell) {
		if (!Helpers.okForm('#form-trayectos')) {
			return
		}
	
		const data = Trayectos.#getFormData()
	
		try {
			// enviar la solicitud de actualización con los datos del formulario
			const id = cell.getRow().getData().id
			let response = await Helpers.fetchData(`${urlAPI}/trayectos/${id}`, {
				method: 'PUT',
				body: data
			})
			
			if (response.message === 'ok') {
				cell.getRow().update(response.data)
				Trayectos.#modal.dispose()
				Toast.info({ message: 'Trayecto actualizado exitosamente' })
			} else {
				Toast.info(
				   { message: 'No se pudo actualizar el trayecto', mode: 'danger', error: response }
				)
			}
		} catch (e) {
			Toast.info(
			   { message: 'Sin acceso a la actualización de trayectos', mode: 'danger', error: e }
			)
		}
	}

	static #deleteRowButton = (cell, formatterParams, onRendered) => `
		<button id="delete-row" class="border-0 bg-transparent" 
				data-bs-toggle="tooltip" title="Eliminar">
			${icons.delete}
		</button>
	`

	static #deleteRowClick = (e, cell) => {
		Trayectos.#currOption = 'delete';
		Trayectos.#modal = new Modal({
			title: 'Eliminar Trayectos',
			content: `
				Confirme la eliminación del trayecto:<br>
				${cell.getRow().getData().origen} - ${cell.getRow().getData().destino}<br>
				Costo: ${cell.getRow().getData().costo}, Duración: ${cell.getRow().getData().duracion}<br>
			`,
			buttons: [
				{
					id: 'add-flights',
					style: 'btn btn-danger',
					html: `${icons.deleteWhite}<span>Eliminar</span>`,
					callBack: () => Trayectos.#delete(cell)
				},
				{
					id: 'cancel-add-flights',
					style: 'btn btn-secundary',
					html: `${icons.xLg}<span>Cancelar</span>`,
					callBack: () => Trayectos.#modal.dispose()
				}
			]
		}).show()
	}

	static async #delete(cell) {
		const id = cell.getRow().getData().id
	
		try {
			let response = await Helpers.fetchData(`${urlAPI}/trayectos/${id}`, {
				method: 'DELETE'
			})
	
			if (response.message === 'ok') {
				cell.getRow().delete()
				this.#modal.dispose()
				Toast.info({ message: 'Trayecto eliminado exitosamente' })
			} else {
				Toast.info(
					{ message: 'No se pudo eliminar el trayecto', mode: 'danger', error: response }
				)
			}
		} catch (e) {
			Toast.info(
				{ message: 'Sin acceso a la eliminación de trayectos', mode: 'danger', error: e }
			)
		}
	}

	static #toComplete(idModal, rowData) {
		if (Trayectos.#currOption === 'edit') {
			document.querySelector(`#${idModal} #origin`).value = rowData.origen
			document.querySelector(`#${idModal} #origin`).disabled = true
			document.querySelector(`#${idModal} #destination`).value = rowData.destino
			document.querySelector(`#${idModal} #destination`).disabled = true
			document.querySelector(`#${idModal} #cost`).value = rowData.costo
			document.querySelector(`#${idModal} #duration`).value = rowData.duration
		}
	}

	static #getFormData() {
		const origen = document.querySelector(`#${Trayectos.#modal.id} #origin`).value
		const destino = document.querySelector(`#${Trayectos.#modal.id} #destination`).value
		const costo = document.querySelector(`#${Trayectos.#modal.id} #cost`).value
		const duracion = document.querySelector(`#${Trayectos.#modal.id} #duration`).value
		return { origen, destino, costo, duracion }
	}
}
