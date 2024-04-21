export default class Usuarios {
	static #table
	static #formUsuarios
	static #modal
	static #currOption

	static #tipos

	constructor() {
		throw new Error('Esta clase no permite el uso del constructor. Utilice Personal.init()')
	}

	static async init() {
		const response = await Helpers.fetchData(`${urlAPI}/usuarios`)
		const data = response.data

		Usuarios.#tipos = [
			{ v: 'Cliente' },
			{ v: 'Auxiliar de Taquilla' },
			{ v: 'Administrador' }
		]

		Usuarios.#formUsuarios = await Helpers.loadPage('./resources/html/form-usuarios.html')

		
		document.querySelector('main').innerHTML = `
			<div class="p-2 w-full">
				<div id="usuarios" class="m-2"></div>
			</dv>
		`
		Usuarios.#table = new Tabulator('#usuarios', {
			height: 'calc(100vh - 150px)', // establecer la altura de la tabla, esto habilita el DOM virtual y mejora drásticamente la velocidad de procesamiento
			data: data, // asignar los datos a la tabla
			layout: 'fitColumns', // ajustar columnas al ancho de la tabla (opcional)
			columns: [
				// definir las columnas de la tabla
				{ title: 'IDENTIFICACIÓN', field: 'id'},
				{ title: 'NOMBRES', field: 'nombres',},
				{ title: 'APELLIDOS', field: 'apellidos',},
				{ title: 'TIPO DE USUARIO', field: 'tipo', hozAlign: 'center' },
				{ formatter: Usuarios.#editRowButton, width: 40, hozAlign: 'center', cellClick: Usuarios.#editRowClick },
				{ formatter: Usuarios.#deleteRowButton, width: 40, hozAlign: 'center', cellClick: Usuarios.#deleteRowClick },
			],
			footerElement: `
				<div class='container-fluid d-flex justify-content-end p-0'>
					<button id='add-usuarios' class='btn btn-info btn-sm'>     
						${icons.plusSquare}&nbsp;&nbsp;Nuevo usuario
					</button>
				</div>
			`.trim()
		})

		// observe que toma automáticamente el ID de cada objeto del array, esto no sucederá con la identificación
		// de los usuarios. Habrá que mapear el array de usuarios y agregar el ID con el mismo valor de la identificación
		// Usuarios.#table.on('rowClick', (e, row) => console.log('Clic sobre la fila con ID: ' + row.getData().id))
		
		// Asigna una respuesta addRow() al evento click
		Usuarios.#table.on('tableBuilt', () => document.querySelector('#add-usuarios').addEventListener('click', Usuarios.#addRow))
		
	}

	/**
	 * Despliega el cuadro de diálogo que permite agregar registros
	 */
	static #addRow = () => {
		Usuarios.#currOption = 'add'
		Usuarios.#modal = new Modal({
			title: 'Añadir Usuarios',
            content: Usuarios.#formUsuarios,
            buttons: [
                {
                    id: 'add-users',
                    style: 'btn btn-primary',
                    html: `${icons.plusSquare}<span> Añadir</span>`,
                    callBack: Usuarios.#add
                },
                {
                    id: 'cancel',
                    style: 'btn btn-secondary',
                    html: `${icons.xLg}<span> Cancelar</span>`,
                    callBack: () => Usuarios.#modal.dispose()
                }
            ],
			built: Usuarios.#toComplete
		}).show()
	}

	static async #add() {
		// forzar la validación de los datos antes de enviarlos al servidor
		if (!Helpers.okForm('#form-usuarios')) {
			return
		}

		const data = Usuarios.#getFormData()
	
		const pw = data.contrasenia
		if (data.tipo === 'Cliente' && pw) {
			Toast.info({ message: 'No se puede crear un cliente con contraseña.', mode: 'danger' })
			return
		}
		if (pw != document.querySelector('#verifypword').value) {
			Toast.info({ message: 'Las contraseñas no coinciden.', mode: 'danger' })
			return
		}

		try {
			// enviar la solicitud de creación con los datos del formulario
            let response = await Helpers.fetchData(`${urlAPI}/usuarios`, {
                method: 'POST',
                body: data
            })

            if (response.message === 'ok') {
				Usuarios.#table.addRow(response.data)
				Usuarios.#modal.dispose()
                Toast.info({ message: 'Se agregó el usuario con éxito.', mode: 'info'})
			} else {
                Toast.info(
					{ message: response.message, mode: 'danger', error: response }
				)
            }
        } catch (e) {
            Toast.info(
				{ message: 'Falló la adición del usuario', mode: 'danger', error: e }
			)
        }
	}

    static #editRowButton = (cell, formatterParams, onRendered) => `
        <button id="edit-row" class="border-0 bg-transparent" data-bs-toggle="tooltip" title="Editar">
            ${icons.editMediumPurple}
        </button>
    `

    static #editRowClick = (e, cell) => {
        Usuarios.#currOption = 'edit'
		Usuarios.#modal = new Modal({
			size: 'default',
			title: 'Actualizar Usuarios',
			content: Usuarios.#formUsuarios,
			buttons: [
				{
					id: 'edit-flights',
					style: 'btn btn-primary',
					html: `${icons.editWhite}&nbsp;&nbsp;<span>Actualizar</span>`,
					callBack: () => Usuarios.#edit(cell)
				},
				{
					id: 'cancel',
					style: 'btn btn-secondary',
					html: `${icons.xLg}<span>Cancelar</span>`,
					callBack: () => Usuarios.#modal.dispose()
				}
			],
			built: idModal => Usuarios.#toComplete(idModal, cell.getRow().getData())
		}).show()
    }

	static async #edit(cell) {
		if (!Helpers.okForm('#form-usuarios')) {
			return
		}
	
		const data = Usuarios.#getFormData()

		if (data.tipo === 'Cliente' && data.contrasenia) {
			Toast.info({ message: 'No se puede crear un cliente con contraseña.', mode: 'danger' })
			return
		}
		if (!data.contrasenia) {
			data.contrasenia = cell.getRow().getData().contrasenia
		} else if (data.contrasenia != document.querySelector('#verifypword').value) {
			Toast.info({ message: 'Las contraseñas no coinciden.', mode: 'danger' })
			return
		}
	
		try {
			// enviar la solicitud de actualización con los datos del formulario
			const id = cell.getRow().getData().id
			let response = await Helpers.fetchData(`${urlAPI}/usuarios/${id}`, {
				method: 'PUT',
				body: data
			})
	
			if (response.message === 'ok') {
				Toast.info({ message: 'Usuario actualizado exitosamente' })
				cell.getRow().update(response.data)
				Usuarios.#modal.dispose()
			} else {
				Toast.info(
				   { message: 'No se pudo actualizar el usuario', mode: 'danger', error: response }
				)
			}
		} catch (e) {
			Toast.info(
			   { message: 'Sin acceso a la actualización de usuarios', mode: 'danger', error: e }
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
		Usuarios.#currOption = 'delete';
		Usuarios.#modal = new Modal({
			title: 'Eliminar Usuarios',
			content: `
				Confirme la eliminación del usuario:<br>
				${cell.getRow().getData().nombres} ${cell.getRow().getData().apellidos}<br>
				ID: ${cell.getRow().getData().id}<br>
			`,
			buttons: [
				{
					id: 'add-flights',
					style: 'btn btn-danger',
					html: `${icons.deleteWhite}<span>Eliminar</span>`,
					callBack: () => Usuarios.#delete(cell)
				},
				{
					id: 'cancel-add-flights',
					style: 'btn btn-secundary',
					html: `${icons.xLg}<span>Cancelar</span>`,
					callBack: () => Usuarios.#modal.dispose()
				}
			]
		}).show()
	}

	static async #delete(cell) {
		const id = cell.getRow().getData().id
	
		try {
			let response = await Helpers.fetchData(`${urlAPI}/usuarios/${id}`, {
				method: 'DELETE'
			})
	
			if (response.message === 'ok') {
				cell.getRow().delete()
				this.#modal.dispose()
				Toast.info({ message: 'Usuario eliminado exitosamente' })
			} else {
				Toast.info(
					{ message: 'No se pudo eliminar el usuario', mode: 'danger', error: response }
				)
			}
		} catch (e) {
			Toast.info(
				{ message: 'Sin acceso a la eliminación de usuarios', mode: 'danger', error: e }
			)
		}
	}

	static #disablePassword() {
		document.querySelector('#type').addEventListener('change', () => {
			if (document.querySelector('#type').value === 'Cliente') {
				document.querySelector('#password').disabled = true
				document.querySelector('#verifypword').disabled = true
			} else {
				document.querySelector('#password').disabled = false
				document.querySelector('#verifypword').disabled = false
			}
		})
	}

	static #toComplete(idModal, rowData) {
		Usuarios.#disablePassword()

		const tipos = Helpers.toOptionList({
			items: Usuarios.#tipos,
			value: 'v',
			text: 'v',
			selected: Usuarios.#currOption === 'edit' ? rowData.tipo : 'Cliente'
		})
	
		document.querySelector(`#${idModal} #type`).innerHTML = tipos

		if (Usuarios.#currOption === 'edit') {
			document.querySelector('#user-id').value = rowData.id
			document.querySelector('#fnames').value = rowData.nombres
			document.querySelector('#lnames').value = rowData.apellidos
			document.querySelector('#nick').value = rowData.perfil
		}
	}

	static #getFormData() {
		const id = 'U-' + document.querySelector('#user-id').value.padStart(5, '0')
		const nombres = document.querySelector('#fnames').value
		const apellidos = document.querySelector('#lnames').value
        const perfil = document.querySelector('#nick').value
        const contrasenia = document.querySelector('#password').value
		const tipo = document.querySelector('#type').value
		return { id, nombres, apellidos, perfil, contrasenia, tipo }
	}
}
