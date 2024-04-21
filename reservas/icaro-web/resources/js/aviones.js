export default class Aviones {
	static #table
	static #formAviones
	static #modal
	static #currOption

	constructor() {
		throw new Error('Esta clase no permite el uso del constructor. Utilice Personal.init()')
	}

	static async init() {
		const response = await Helpers.fetchData(`${urlAPI}/sillas/total`)
		const data = response.data

		document.querySelector('main').innerHTML = `
			<div class="p-2 w-full">
				<div id="aviones" class="m-2"></div>
			</dv>
		`

		Aviones.#formAviones = await Helpers.loadPage('./resources/html/form-aviones.html')
		Aviones.#table = new Tabulator('#aviones', {
			height: 'calc(100vh - 150px)', // establecer la altura de la tabla, esto habilita el DOM virtual y mejora drásticamente la velocidad de procesamiento
			data: data, // asignar los datos a la tabla
			layout: 'fitColumns', // ajustar columnas al ancho de la tabla (opcional)
			columns: [
				// definir las columnas de la tabla
				{ title: 'PLACA', field: 'placa', hozAlign: 'center' },
				{ title: 'MODELO', field: 'modelo', hozAlign: 'left' },
				{ title: 'EJECUTIVAS', field: 'totalSillas.ejecutivas', hozAlign: 'center' },
				{ title: 'ECONÓMICAS', field: 'totalSillas.economicas', hozAlign: 'center' },
				{ formatter: Aviones.#editRowButton, width: 40, hozAlign: 'center', cellClick: Aviones.#editRowClick },
				{ formatter: Aviones.#deleteRowButton, width: 40, hozAlign: 'center', cellClick: Aviones.#deleteRowClick },
			],
			footerElement: `
				<div class='container-fluid d-flex justify-content-end p-0'>
					<button id='add-aviones' class='btn btn-info btn-sm'>     
						${icons.plusSquare}&nbsp;&nbsp;Nuevo avión
					</button>
				</div>
			`.trim()
		})

		// observe que toma automáticamente el ID de cada objeto del array, esto no sucederá con la identificación
		// de los aviones. Habrá que mapear el array de aviones y agregar el ID con el mismo valor de la identificación
		Aviones.#table.on('rowClick', (e, row) => console.log('Clic sobre la fila con ID: ' + row.getData().id))
		// Asigna una respuesta addRow() al evento click
		Aviones.#table.on('tableBuilt', () => document.querySelector('#add-aviones').addEventListener('click', Aviones.#addRow))
	}

	/**
	 * Despliega el cuadro de diálogo que permite agregar registros
	 * @param {Event} e
	 */
	 static #addRow = async e => {
		Aviones.#currOption = 'add'
		Aviones.#modal = new Modal({
			height: 'calc(100vh - 150px)', // establecer la altura de la tabla, esto habilita el DOM virtual y mejora drásticamente la velocidad de procesamiento
			title: 'Añadir Aviones',
            content: Aviones.#formAviones,
            buttons: [
                {
                    id: 'add-flights',
                    style: 'btn btn-primary',
                    html: `${icons.plusSquare}<span> Añadir</span>`,
                    callBack: Aviones.#add
                },
                {
                    id: 'cancel',
                    style: 'btn btn-secondary',
                    html: `${icons.xLg}<span> Cancelar</span>`,
                    callBack: () => Aviones.#modal.dispose()
                }
            ],
			built: Aviones.#toComplete
		}).show()
	}

	static async #add() {
		// forzar la validación de los datos antes de enviarlos al servidor
		if (!Helpers.okForm('#form-aviones')) {
			return
		}
	
		let data = Aviones.#getFormData()
	
		try {
			// enviar la solicitud de creación con los datos del formulario
			let avionesRes = await Helpers.fetchData(`${urlAPI}/aviones`, {
				method: 'POST',
				body: { placa: data.placa, modelo: data.modelo }
			})

			// Se añaden al avión sus respectivas sillas
			let sillasRes = await Helpers.fetchData(`${urlAPI}/sillas`, {
				method: 'POST',
				body: {
					avion: data.placa,
					filasEjecutivas: data.filasEjecutivas,
					totalSillas: data.totalSillas
				}
			})			
	
			if (avionesRes.message === 'ok' && sillasRes.message === 'ok') {
				Aviones.#table.addRow(avionesRes.data)
				Aviones.#modal.dispose()
				Toast.info({ message: 'Avión agregado exitosamente' })
			} else {
				Toast.info(
				   { message: 'No se agregó el avión', mode: 'danger', error: avionesRes }
				)
			}
		} catch (e) {
			Toast.info(
			   { message: 'Sin acceso a la creación de aviones', mode: 'danger', error: e }
			)
		}
	}

    static #editRowButton = (cell, formatterParams, onRendered) => `
        <button id="edit-row" class="border-0 bg-transparent" data-bs-toggle="tooltip" title="Editar">
            ${icons.editMediumPurple}
        </button>
    `

    static #editRowClick = (e, cell) => {
        Aviones.#currOption = 'edit'
		Aviones.#modal = new Modal({
			size: 'default',
			title: 'Actualizar Aviones',
			content: Aviones.#formAviones,
			buttons: [
				{
					id: 'edit-flights',
					style: 'btn btn-primary',
					html: `${icons.editWhite}&nbsp;&nbsp;<span>Actualizar</span>`,
					callBack: () => Aviones.#edit(cell)
				},
				{
					id: 'cancel',
					style: 'btn btn-secondary',
					html: `${icons.xLg}<span>Cancelar</span>`,
					callBack: () => Aviones.#modal.dispose()
				}
			],
			built: idModal => Aviones.#toComplete(idModal, cell.getRow().getData())
		}).show()
    }

	static async #edit(cell) {
		if (!Helpers.okForm('#form-aviones')) {
			return
		}
	
		const data = Aviones.#getFormData()
	
		try {
			// enviar la solicitud de actualización con los datos del formulario
			const id = cell.getRow().getData().placa
			let avionesRes = await Helpers.fetchData(`${urlAPI}/aviones/${id}`, {
				method: 'PUT',
				body: data
			})

			// Se añaden al avión sus respectivas sillas
			let sillasRes = await Helpers.fetchData(`${urlAPI}/sillas`, {
				method: 'POST',
				body: {
					avion: data.placa,
					filasEjecutivas: data.filasEjecutivas,
					totalSillas: data.totalSillas
				}
			})
			
			if (avionesRes.message === 'ok' && sillasRes.message === 'ok') {
				cell.getRow().update({
					placa: avionesRes.data.placa,
					modelo: avionesRes.data.modelo,
					totalSillas: {
						ejecutivas: data.filasEjecutivas,
						economicas: data.totalSillas - 4 * data.filasEjecutivas
					}
				})
				Aviones.#modal.dispose()
				Toast.info({ message: 'Avión actualizado exitosamente' })
			} else {
				Toast.info(
				   { message: 'No se pudo actualizar el avión', mode: 'danger', error: avionesRes }
				)
			}
		} catch (e) {
			Toast.info(
			   { message: 'Sin acceso a la actualización de aviones', mode: 'danger', error: e }
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
		Aviones.#currOption = 'delete';
		Aviones.#modal = new Modal({
			title: 'Eliminar Aviones',
			content: `
				Confirme la eliminación del avión:<br>
				${cell.getRow().getData().placa} - ${cell.getRow().getData().modelo}<br>
			`,
			buttons: [
				{
					id: 'add-flights',
					style: 'btn btn-danger',
					html: `${icons.deleteWhite}<span>Eliminar</span>`,
					callBack: () => Aviones.#delete(cell)
				},
				{
					id: 'cancel-add-flights',
					style: 'btn btn-secundary',
					html: `${icons.xLg}<span>Cancelar</span>`,
					callBack: () => Aviones.#modal.dispose()
				}
			]
		}).show()
	}

	static async #delete(cell) {
		const id = cell.getRow().getData().placa
	
		try {
			let response = await Helpers.fetchData(`${urlAPI}/aviones/${id}`, {
				method: 'DELETE'
			})
	
			if (response.message === 'ok') {
				cell.getRow().delete()
				this.#modal.dispose()
				Toast.info({ message: 'Avión eliminado exitosamente' })
			} else {
				Toast.info(
					{ message: 'No se pudo eliminar el avión', mode: 'danger', error: response }
				)
			}
		} catch (e) {
			Toast.info(
				{ message: 'Sin acceso a la eliminación de aviones', mode: 'danger', error: e }
			)
		}
	}

	static #toComplete(idModal, rowData) {	
		if (Aviones.#currOption === 'edit') {
			document.querySelector(`#${idModal} #plate`).disabled = true
			document.querySelector(`#${idModal} #plate`).value = rowData.placa
			document.querySelector(`#${idModal} #model`).value = rowData.modelo
			document.querySelector(`#${idModal} #executives`).value = 
			document.querySelector(`#${idModal} #totalSeats`).value = true
		}
	}

	static #getFormData() {
		const placa = document.querySelector(`#${Aviones.#modal.id} #plate`).value
		const modelo = document.querySelector(`#${Aviones.#modal.id} #model`).value
		const filasEjecutivas = document.querySelector(`#${Aviones.#modal.id} #executives`).value
		const totalSillas = document.querySelector(`#${Aviones.#modal.id} #totalSeats`).value
		return { placa, modelo, filasEjecutivas, totalSillas }
	}
}
