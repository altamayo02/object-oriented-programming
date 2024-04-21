# Referencias recomendadas

- [Introduction to JavaScript](https://enacit1.epfl.ch/tutorials/JavaScript/hierarchy.html)

- [W3 Schools - página principal](https://www.w3schools.com/html/default.asp)
- [W3 Schools - Tipos de entradas HTML](https://www.w3schools.com/html/html_form_input_types.asp)
- [W3 Schools - CSS](https://www.w3schools.com/css/default.asp)
- [W3 Schools - Formularios](https://www.w3schools.com/html/html_forms.asp)
- [Bootstrap - Descarga](https://getbootstrap.com/docs/5.2/getting-started/download/)
- [Bootstrap - Introducción](https://getbootstrap.com/docs/5.2/getting-started/introduction/)
- [Bootswatch - Estilos disponibles](https://bootswatch.com/)
- [Bootswatch - Flatly Style](https://bootswatch.com/flatly/) 
- [Tabulator](https://tabulator.info/)
- [Popper](https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.6/dist/umd/)  << OJO>>

Ver el archivo *./resources/utils/léame.md* para más información
------------

post("/autenticar", (request, response) -> {
    JSONObject json = new JSONObject(request.body());
    json = usuariosService.authenticate(json);
    return new StandardResponse(response, json.getString("ok"), json.getJSONObject("usuario"));
});

------------

public JSONObject authenticate(JSONObject json) throws Exception {
    Convertir la contraseña recibida a MD5
    Buscar el usuario con la identificación recibida
    
    Si la contraseña recibida no coincide con la del usuario
        lanzar una excepción
        
    retornar "ok" y los datos del usuario autenticado
}

