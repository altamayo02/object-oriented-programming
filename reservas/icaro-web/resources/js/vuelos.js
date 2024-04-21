export default class Vuelos {
    static #currOption // add | edit | delete según la elección del usuario
    static #table // la instancia de tipo Tabulator
    static #formVuelos // el código html del formulario que se inyecta en el modal
    static #modal // la ventana modal para agregar, editar y eliminar

	static #trayectos // un array de instancias de trayectos
	static #aviones // un array de instancias de aviones
	static #estados

	constructor() {
        throw new Error('Esta clase no permite el uso de un constructor. Utilice init() en vez.')
    }

	static async init() {
		const response = await Helpers.fetchData(`${urlAPI}/vuelos`)
		const data = response.data

		// leer los trayectos y concatener origen, destino y costo en cada objeto  
		Vuelos.#trayectos = await Helpers.fetchData(`${urlAPI}/trayectos`)
		Vuelos.#trayectos.data.forEach(
			t => (t.toString = `${t.id} ${t.origen} - ${t.destino} ($${t.costo})`)
		)

		// leer los aviones y concatenar la matrícula con el modelo  
		Vuelos.#aviones = await Helpers.fetchData(`${urlAPI}/aviones`)
		Vuelos.#aviones.data.forEach(
			a => (a.toString = `${a.placa} - ${a.modelo}`)
		)

		// TODO - This could be a GET request
		Vuelos.#estados = [
			{ v: 'Programado' },
			{ v: 'A tiempo' },
			{ v: 'Retrasado' },
			{ v: 'Cancelado' }
		]

		// cargar el código fuente del formulario para el ingreso y edición de vuelos
		Vuelos.#formVuelos = await Helpers.loadPage(`./resources/html/form-vuelos.html`)


		document.querySelector('main').innerHTML = `
			<div class="p-2 w-full">
				<div id="vuelos" class="m-2"></div>
			</dv>
		`

		Vuelos.#table = new Tabulator(`#vuelos`, {
			// establecer la altura de la tabla, esto habilita el DOM virtual y mejora drásticamente la velocidad de procesamiento
			// height: 'calc(100vh - 150px)',
			size: 'default',
			data: data, // asignar los datos a la tabla
			layout: 'fitColumns', // ajustar columnas al ancho de la tabla (opcional)
			columns: [
				// definir las columnas de la tabla
				{ title: 'ORIGEN', field: 'trayecto.origen', width: 150 },
				{ title: 'DESTINO', field: 'trayecto.destino', hozAlign: 'left' },
				{ title: 'AVIÓN', field: 'avion.placa', hozAlign: 'left' },
				{ title: 'FECHA/HORA', field: 'fechaHora', hozAlign: 'center', formatter: 'datetime',
					formatterParams: {
						inputFormat: 'iso',
						outputFormat: 'yyyy-MM-dd hh:mm a',
						invalidPlaceholder: 'Fecha y hora inválidas'
					}
				},
				{ title: 'DURACIÓN', field: 'trayecto.duracion', hozAlign: 'center',
					formatter: (cell) => Duration.fromISO(cell.getValue()).toFormat('hh:mm')
				},
				{ title: 'COSTO', field: 'trayecto.costo', hozAlign: 'center', formatter: 'money' },
				{ title: 'ESTADO', field: 'estado', hozAlign: 'center' },
				// TODO - Could be an only function
				{ formatter: Vuelos.#editRowButton, width: 40, hozAlign: 'center', cellClick: Vuelos.#editRowClick },
				{ formatter: Vuelos.#deleteRowButton, width: 40, hozAlign: 'center', cellClick: Vuelos.#deleteRowClick },
			],
			footerElement: `
				<div class='container-fluid d-flex justify-content-end p-0'>
					<button id='add-vuelos' class='btn btn-info btn-sm'>     
						${icons.plusSquare}&nbsp;&nbsp;Nuevo vuelo
					</button>
				</div>
			`.trim()
		})

		// Manager.#table.on('rowClick', (e, row) => console.log(`Clicked over row: ${row.getData().id}`))
		Vuelos.#table.on('tableBuilt', () => document.querySelector(`#add-vuelos`).addEventListener('click', Vuelos.#addRow))
	}

	/**
	 * Despliega el cuadro de diálogo que permite agregar registros
	 */
	static #addRow = () => {
		Vuelos.#currOption = 'add'
		Vuelos.#modal = new Modal({
			height: 'calc(100vh - 150px)', // establecer la altura de la tabla, esto habilita el DOM virtual y mejora drásticamente la velocidad de procesamiento
			title: 'Añadir Vuelos',
            content: Vuelos.#formVuelos,
            buttons: [
                {
                    id: 'add-flights',
                    style: 'btn btn-primary',
                    html: `${icons.plusSquare}<span> Añadir</span>`,
                    callBack: Vuelos.#add
                },
                {
                    id: 'cancel',
                    style: 'btn btn-secondary',
                    html: `${icons.xLg}<span> Cancelar</span>`,
                    callBack: () => Vuelos.#modal.dispose()
                }
            ],
			built: Vuelos.#toComplete
		}).show()
	}

	static async #add() {
		// forzar la validación de los datos antes de enviarlos al servidor
		if (!Helpers.okForm('#form-vuelos')) {
			return
		}
	
		let data = Vuelos.#getFormData()
		data.estado = "Programado"
	
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
				Toast.info(
				   { message: 'No se agregó el vuelo', mode: 'danger', error: response }
				)
			}
		} catch (e) {
			Toast.info(
			   { message: 'Sin acceso a la creación de vuelos', mode: 'danger', error: e }
			)
		}
	}

    static #editRowButton = (cell, formatterParams, onRendered) => `
        <button id="edit-row" class="border-0 bg-transparent" data-bs-toggle="tooltip" title="Editar">
            ${icons.editMediumPurple}
        </button>
    `

    static #editRowClick = (e, cell) => {
		Vuelos.#currOption = 'edit'
		Vuelos.#modal = new Modal({
			size: 'default',
			title: 'Actualizar Vuelos',
			content: Vuelos.#formVuelos,
			buttons: [
				{
					id: 'edit-flights',
					style: 'btn btn-primary',
					html: `${icons.editWhite}&nbsp;&nbsp;<span>Actualizar</span>`,
					callBack: () => Vuelos.#edit(cell)
				},
				{
					id: 'cancel',
					style: 'btn btn-secondary',
					html: `${icons.xLg}<span>Cancelar</span>`,
					callBack: () => Vuelos.#modal.dispose()
				}
			],
			built: idModal => Vuelos.#toComplete(idModal, cell.getRow().getData())
		}).show()
	}

	static async #edit(cell) {
		if (!Helpers.okForm('#form-vuelos')) {
			return
		}
	
		const data = Vuelos.#getFormData()
	
		try {
			// enviar la solicitud de actualización con los datos del formulario
			const id = cell.getRow().getData().id
			let response = await Helpers.fetchData(`${urlAPI}/vuelos/${id}`, {
				method: 'PUT',
				body: data
			})

	
			if (response.message === 'ok') {
				Toast.info({ message: 'Vuelo actualizado exitosamente' })
				cell.getRow().update(response.data)
				Vuelos.#modal.dispose()
			} else {
				Toast.info(
				   { message: 'No se pudo actualizar el vuelo', mode: 'danger', error: response }
				)
			}
		} catch (e) {
			Toast.info(
			   { message: 'Sin acceso a la actualización de vuelos', mode: 'danger', error: e }
			)
		}
	}

	static #deleteRowButton = (cell, formatterParams, onRendered) => `
		<button id="delete-row" class="border-0 bg-transparent" data-bs-toggle="tooltip" title="Eliminar">
			${icons.delete}
		</button>
	`

	static #deleteRowClick = (e, cell) => {
		Vuelos.#currOption = 'delete';
		Vuelos.#modal = new Modal({
			title: 'Eliminar Vuelos',
			content: `
				Confirme la eliminación del vuelo:<br>
				${cell.getRow().getData().trayecto.origen} - 
				${cell.getRow().getData().trayecto.destino}<br>
				Fecha y hora: ${DateTime.fromISO(
				cell.getRow().getData().fechaHora
				).toFormat('yyyy-MM-dd hh:mm a')}<br>
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
		}).show()
	}

	static async #delete(cell) {
		const id = cell.getRow().getData().id
	
		try {
			let response = await Helpers.fetchData(`${urlAPI}/vuelos/${id}`, {
				method: 'DELETE'
			})
	
			if (response.message === 'ok') {
				Toast.info({ message: 'Vuelo eliminado exitosamente' })
				cell.getRow().delete()
				this.#modal.dispose()
			} else {
				Toast.info(
					{ message: 'No se pudo eliminar el vuelo', mode: 'danger', error: response }
				)
			}
		} catch (e) {
			Toast.info(
				{ message: 'Sin acceso a la eliminación de vuelos', mode: 'danger', error: e }
			)
		}
	}

	static #toComplete(idModal, rowData) {
		// crear la lista de opciones para el select de trayectos
		const trayectos = Helpers.toOptionList({
			items: Vuelos.#trayectos.data,
			value: 'id',
			text: 'toString',
			selected: Vuelos.#currOption === 'edit' ? rowData.trayecto.id : ''
		})
	
		document.querySelector(`#${idModal} #journeys`).innerHTML = trayectos
	
		// crear la lista de opciones para el select de aviones
		const aviones = Helpers.toOptionList({
			items: Vuelos.#aviones.data,
			value: 'placa',
			text: 'toString',
			selected: Vuelos.#currOption === 'edit' ? rowData.avion.placa : ''
		})
	
		document.querySelector(`#${idModal} #aircrafts`).innerHTML = aviones
	
		if (Vuelos.#currOption === 'add') {
			document.querySelector(`#${idModal} #states`).disabled = true
		} else if (Vuelos.#currOption === 'edit') {
			document.querySelector(`#${idModal} #datetime`).value = rowData.fechaHora
			
			const estados = Helpers.toOptionList({
				items: Vuelos.#estados,
				value: 'v',
				text: 'v',
				selected: rowData.estado
			})

			document.querySelector(`#${idModal} #states`).innerHTML = estados
		}
	}

	static #getFormData() {
		const fechaHora = document.querySelector(`#${Vuelos.#modal.id} #datetime`).value
		const idTrayecto = document.querySelector(`#${Vuelos.#modal.id} #journeys`).value
		const placaAvion = document.querySelector(`#${Vuelos.#modal.id} #aircrafts`).value
		const estado = document.querySelector(`#${Vuelos.#modal.id} #states`).value
		return { fechaHora, idTrayecto, placaAvion, estado }
	}
}
