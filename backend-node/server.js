const express = require("express");
const mysql = require("mysql2");
const cors = require("cors");
require("dotenv").config();

const app = express();
app.use(express.json());
app.use(cors());

// ✅ Create a MySQL connection
const db = mysql.createPool({
    host: process.env.DB_HOST,
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    database: process.env.DB_NAME,
    port: process.env.DB_PORT,
    waitForConnections: true,
    connectionLimit: 30,
    queueLimit: 0,
    idleTimeout: 60000,
});

// ✅ Test MySQL connection
db.query("SELECT 1", (err) => {
    if (err) {
        console.error("❌ Database connection failed:", err);
    } else {
        console.log("✅ Connected to MySQL!");
    }
});

// Detect errors in the connection pool
db.on('error', (err) => {
    console.error(`[${getTimestamp()}] ❌ Error crítico en la conexión a la base de datos:`, err.message);
});

// ✅ Test Route
app.get("/", (req, res) => {
    res.send("API is running...");
});

// Querys

// Obtiene a todos los usuarios
app.get("/users", (req, res) => {
    db.query("SELECT * FROM Usuario", (err, results) => {
        if (err) {
            console.error(`[${getTimestamp()}] ❌ Error al obtener usuarios:`, err.message);
            return res.status(500).json({ error: err.message });
        }
        res.json(results);
    });
});

// Obtiene todos los sectores
app.get("/pdv", (req, res) => {
    db.query("SELECT * FROM PDV", (err, results) => {
        if (err) {
            console.error(`[${getTimestamp()}] ❌ Error al obtener PDVs:`, err.message);
            return res.status(500).json({ error: err.message });
        }
        res.json(results);
    });
});

//GET mesa por ID
app.get("/mesa/:idMesa", (req, res) => {
    const idMesa = req.params.idMesa;
    db.query("SELECT * FROM Mesa WHERE IDMesa = ?", [idMesa], (err, results) => {
        if (err) {
            console.error(`[${getTimestamp()}] ❌ Error al obtener mesa:`, err.message);
            return res.status(500).json({ error: err.message });
        }
        res.json(results[0]);
    });
});

//GET mesas por sector
app.get("/mesas/:idPdv", (req, res) => {
    const idPdv = req.params.idPdv;
    db.query(`SELECT Mesa.*, PDV.*,
        EXISTS(SELECT 1 FROM Comensal c WHERE c.IDMesa = Mesa.IDMesa) AS TieneComensales
        FROM Mesa
        INNER JOIN PDV ON Mesa.IDPDV = PDV.IDPDV
        WHERE Mesa.IDPDV = ?`, [idPdv], (err, results) => {
        if (err) {
            console.error(`[${getTimestamp()}] ❌ Error al obtener mesas:`, err.message);
            return res.status(500).json({ error: err.message });
        }
        res.json(results);
    });
});

app.get("/comensales/mesa/:idMesa", (req, res) => {
    const idMesa = req.params.idMesa;
    db.query("SELECT * FROM Comensal WHERE IDMesa = ?", [idMesa], (err, results) => {
        if (err) {
            console.error(`[${getTimestamp()}] ❌ Error al obtener comensales de la mesa:`, err.message);
            return res.status(500).json({ error: err.message });
        }
        res.json(results);
    });
});

// Ruta para calcular el total de un comensal de una mesa
app.get("/totalComensal/comensal/:idComensal", (req, res) => {
    const idComensal = req.params.idComensal;
    db.query(`SELECT 
        (SELECT COALESCE(SUM(p.ValorProducto * cp.Cantidad), 0)
        FROM Comensal_Producto cp
        JOIN Producto p ON cp.IDProducto = p.IDProducto
        WHERE cp.IDComensal = c.IDComensal)
        +
        (SELECT COALESCE(SUM(e.ValorExtra * cpe.Cantidad), 0)
        FROM Comensal_Producto_Extra cpe
        JOIN Extra e ON cpe.IDExtra = e.IDExtra
        WHERE cpe.IDComensal = c.IDComensal) AS TotalPedido 
        FROM Comensal c WHERE c.IDComensal = ?;`, [idComensal], (err, results) => {
        if (err) {
            console.error(`[${getTimestamp()}] ❌ Error al calcular total del comensal:`, err.message);
            return res.status(500).json({ error: err.message });
        }
        res.json(results);
    });
});

app.get("/totalMesa/mesa/:idMesa", (req, res) => {
    const idMesa = req.params.idMesa;
    db.query(`SELECT
        (SELECT COALESCE(SUM(p.ValorProducto * cp.Cantidad), 0)
        FROM Comensal c
        JOIN Comensal_Producto cp ON c.IDComensal = cp.IDComensal
        JOIN Producto p ON cp.IDProducto = p.IDProducto
        WHERE c.IDMesa = ? and c.Pagado = 0)
        +
        (SELECT COALESCE(SUM(e.ValorExtra * cpe.Cantidad), 0)
        FROM Comensal c
        JOIN Comensal_Producto_Extra cpe ON c.IDComensal = cpe.IDComensal
        JOIN Extra e ON cpe.IDExtra = e.IDExtra
        WHERE c.IDMesa = ? and c.Pagado = 0)
        AS TotalMesa;`, [idMesa, idMesa], (err, results) => {
        if (err) {
            console.error(`[${getTimestamp()}] ❌ Error al calcular total de la mesa:`, err.message);
            return res.status(500).json({ error: err.message });
        }
        res.json(results);
    });
});

// GET para obtener todos los detalles de una mesa, comensales, pedidos y extras
app.get("/detallesmesa/:idMesa", (req, res) => {
    const idMesa = req.params.idMesa;
    db.query(`SELECT
        C.IDComensal, # Id del comensal
        C.NombreComensal,  #nombre del comensal
        C.IDMesa,   # id de la mesa en la se encuentra el comensal
        P.NombreProducto,
        P.IDCategoria,
        P.IDPDV,
        P.IDProducto,   # id del producto que posee el comensal
        P.ValorProducto,    # valor individual del producto
        CP.IDComensal AS CP_IDComensal,     # id del comensal que posee un producto con esta id
        CP.IDProducto AS CP_IDProducto,     # id del producto
        CP.Cantidad AS Producto_Cantidad,     # cantidad de productos que pidio el comensal
        CP.Entregado AS Producto_Entregado,   # si el producto fue entregado
        CP.Notas,
        CP.Instancia,
        E.IDExtra,  # id del extra que pidio el comensal
        E.NombreExtra,  # nombre del extra
        E.ValorExtra,   # valor extra agregado al pedido del comensal
        CPE.IDComensal AS CPE_IDComensal,   # id del comensal
        CPE.IDProducto AS CPE_IDProducto,   # id del producto
        CPE.IDExtra AS CPE_IDExtra, # id del extra
        CPE.Cantidad AS Cantidad_Extra,    # cantidad de extras que solicito el comensal
        CPE.Instancia AS CPE_Instancia
        FROM Comensal C
        JOIN Comensal_Producto CP ON C.IDComensal = CP.IDComensal
        JOIN Producto P ON CP.IDProducto = P.IDProducto
        LEFT JOIN Comensal_Producto_Extra CPE ON CP.IDComensal = CPE.IDComensal
            AND CP.IDProducto = CPE.IDProducto
            AND CP.Instancia = CPE.Instancia
        LEFT JOIN Extra E ON CPE.IDExtra = E.IDExtra
        WHERE C.IDMesa = ?;`, [idMesa], (err, results) => {
        if (err) {
            console.error(`[${getTimestamp()}] ❌ Error al obtener detalles de la mesa:`, err.message);
            return res.status(500).json({ error: err.message });
        }
        res.json(results);
    });
});

//GET categorias
app.get("/categorias/:idMesa", (req, res) => {
    const idMesa = req.params.idMesa;
    db.query(`SELECT c.*
        FROM categoria c
        JOIN mesa m ON c.IDPDV = m.IDPDV
        WHERE m.IDMesa = ?
        order by c.NombreCategoria;`, [idMesa], (err, results) => {
        if (err) {
            console.error(`[${getTimestamp()}] ❌ Error al obtener categorías:`, err.message);
            return res.status(500).json({ error: err.message });
        }
        res.json(results);
    });
});

// GET categorias por PDV
app.get("/categorias/pdv/:idPDV", (req, res) => {
    const idPDV = req.params.idPDV;
    db.query("SELECT * FROM categoria WHERE IDPDV = ?", [idPDV], (err, results) => {
        if (err) {
            console.error(`[${getTimestamp()}] ❌ Error al obtener categorías por PDV:`, err.message);
            return res.status(500).json({ error: err.message });
        }
        res.json(results);
    });
});

//GET productos por categoría
app.get("/productos/categoria/:idCategoria", (req, res) => {
    const idCategoria = req.params.idCategoria;
    db.query("SELECT * FROM producto INNER JOIN categoria WHERE " +
        "categoria.IDCategoria = producto.IDCategoria and producto.IDCategoria = ?",
        [idCategoria], (err, results) => {
            if (err) {
                console.error(`[${getTimestamp()}] ❌ Error al obtener productos por categoría:`, err.message);
                return res.status(500).json({ error: err.message });
            }
            res.json(results);
        });
});

//GET producto por id
app.get("/producto/:idProducto", (req, res) => {
    const idProducto = req.params.idProducto;
    db.query("SELECT * FROM producto WHERE IDProducto = ?", [idProducto], (err, results) => {
        if (err) {
            console.error(`[${getTimestamp()}] ❌ Error al obtener producto por ID:`, err.message);
            return res.status(500).json({ error: err.message });
        }
        res.json(results[0]);
    });
});

//GET extras
app.get("/extras", (req, res) => {
    db.query("SELECT * FROM extra", (err, results) => {
        if (err) {
            console.error(`[${getTimestamp()}] ❌ Error al obtener extras:`, err.message);
            return res.status(500).json({ error: err.message });
        }
        res.json(results);
    });
});

//GET productos por pdv
app.get("/productos/pdv/:idPDV", (req, res) => {
    const idPDV = req.params.idPDV;
    db.query("SELECT * FROM producto INNER JOIN pdv WHERE " +
        "pdv.IDPDV = producto.IDPDV and producto.IDPDV = ?",
        [idPDV], (err, results) => {
            if (err) {
                console.error(`[${getTimestamp()}] ❌ Error al obtener productos por PDV:`, err.message);
                return res.status(500).json({ error: err.message });
            }
            res.json(results);
        });
})

//GET comensales por mesa
app.get("/comensales/mesa/:idMesa", (req, res) => {
    const idMesa = req.params.idMesa;
    db.query("SELECT * FROM comensal WHERE IDMesa = ?", [idMesa], (err, results) => {
        if (err) {
            console.error(`[${getTimestamp()}] ❌ Error al obtener comensales por mesa:`, err.message);
            return res.status(500).json({ error: err.message });
        }
        res.json(results);
    });
});

//GET informacion de un comensal
app.get("/comensal/:idComensal", (req, res) => {
    const idComensal = req.params.idComensal;
    db.query("SELECT * FROM comensal WHERE IDComensal = ?", [idComensal], (err, results) => {
        if (err) {
            console.error(`[${getTimestamp()}] ❌ Error al obtener información del comensal:`, err.message);
            return res.status(500).json({ error: err.message });
        }
        res.json(results);
    });
});

//GET productos de un comensal
app.get("/comensal/productos/:idComensal", (req, res) => {
    const idComensal = req.params.idComensal;
    db.query(`SELECT
        C.IDComensal, # Id del comensal
        P.NombreProducto,
        P.IDCategoria,
        P.IDPDV,
        P.IDProducto,   # id del producto que posee el comensal
        P.ValorProducto,    # valor individual del producto
        CP.IDComensal AS CP_IDComensal,     # id del comensal que posee un producto con esta id
        CP.IDProducto AS CP_IDProducto,     # id del producto
        CP.Cantidad AS Producto_Cantidad,     # cantidad de productos que pidio el comensal
        CP.Entregado AS Producto_Entregado,   # si el producto fue entregado
        CP.Notas,
        CP.Instancia,
        E.IDExtra,  # id del extra que pidio el comensal
        E.NombreExtra,  # nombre del extra
        E.ValorExtra,   # valor extra agregado al pedido del comensal
        CPE.IDComensal AS CPE_IDComensal,   # id del comensal
        CPE.IDProducto AS CPE_IDProducto,   # id del producto
        CPE.IDExtra AS CPE_IDExtra, # id del extra
        CPE.Cantidad AS Cantidad_Extra,    # cantidad de extras que solicito el comensal
        CPE.Instancia AS CPE_Instancia  #instancia de comensal_producto_extra
        FROM Comensal C
        JOIN Comensal_Producto CP ON C.IDComensal = CP.IDComensal
        JOIN Producto P ON CP.IDProducto = P.IDProducto
        LEFT JOIN Comensal_Producto_Extra CPE ON CP.IDComensal = CPE.IDComensal
            AND CP.IDProducto = CPE.IDProducto
            AND CP.Instancia = CPE.Instancia
        LEFT JOIN Extra E ON CPE.IDExtra = E.IDExtra
        WHERE C.IDComensal = ?`, [idComensal], (err, results) => {
        if (err) {
            console.error(`[${getTimestamp()}] ❌ Error al obtener productos del comensal:`, err.message);
            return res.status(500).json({ error: err.message });
        }
        //console.log(results);
        res.json(results);
    });
});

// POST para agregar un comensal
app.post("/agregarComensal/:nombreComensal/:idMesa", (req, res) => {
    const { nombreComensal, idMesa } = req.params;
    db.query("INSERT INTO comensal (NombreComensal, IDMesa, Pagado) VALUES (?, ?, 0)", [nombreComensal, idMesa], (err, result) => {
        if (err) {
            console.error(`[${getTimestamp()}] ❌ Error al agregar comensal:`, err.message);
            return res.status(500).json({ error: err.message });
        }
        res.json({ message: "Comensal agregado correctamente" });
    });
});

//INSERT producto
/*
app.post("/agregarProducto/:idComensal/:idProducto/:cantidad/:entregado/:notas", (req, res) => {
    const idComensal = req.params.idComensal;
    const idProducto = req.params.idProducto;
    const cantidad = parseInt(req.params.cantidad);
    const entregado = req.params.entregado === 'true';
    let notas = decodeURIComponent(req.params.notas);

    if (!notas || notas.toLowerCase() === "null" || notas.trim() === "") {
        notas = " ";
    }

    console.log("📥 Datos recibidos:", {
        idComensal,
        idProducto,
        cantidad,
        entregado,
        notasOriginal: notas
    });

    // Obtener la próxima instancia disponible para esa combinación de comensal + producto
    const getMaxInstanciaQuery = `
            SELECT MAX(Instancia) AS maxInstancia
            FROM comensal_producto
            WHERE IDComensal = ? AND IDProducto = ? AND Notas = ?`;

    const rondaRegex = /Ronda\s+(\d+)/i;
    const notasBase = notas.trim();

    db.query(
        `SELECT Notas, Entregado FROM comensal_producto
         WHERE IDComensal = ? AND IDProducto = ?`,
        [idComensal, idProducto],
        (err, results) => {
            if (err) {
                console.error("❌ Error al consultar notas previas:", err);
                return res.status(500).json({ error: err.message });
            }

            let notaFinal = notas;
            let maxRonda = 1;
            let seDebeAumentarRonda = false;

            // Filtrar las notas que tienen el mismo contenido base (sin encabezado de ronda)
            const notasFiltradas = results.filter(row => {
                const notaSinRonda = row.Notas?.replace(rondaRegex, '').trim() || "";
                return notaSinRonda === notasBase;
            });

            notasFiltradas.forEach(row => {
                const nota = row.Notas?.trim() || "";
                const entregadoPrevio = !!row.Entregado;

                const match = nota.match(rondaRegex);
                if (match) {
                    const nro = parseInt(match[1]);
                    if (nro > maxRonda) {
                        maxRonda = nro;
                        if (entregadoPrevio) {
                            seDebeAumentarRonda = true;
                        }
                    }
                } else {
                    // Nota sin ronda explícita, tratada como base
                    if (entregadoPrevio) {
                        seDebeAumentarRonda = true;
                    }
                }
            });

            if (notasFiltradas.length === 0) {
                notaFinal = notas;
                console.log("🟢 Primera vez con esta nota. Se inserta como está:", notaFinal);
            } else if (seDebeAumentarRonda) {
                const nuevaRonda = `Ronda ${maxRonda + 1}`;
                notaFinal = notas.trim()
                    ? `${nuevaRonda} - ${notas}`
                    : nuevaRonda;
                console.log("📝 Ronda incrementada. Nota final:", notaFinal);
            } else {
                console.log("⏸️ Nota repetida no entregada. Se mantiene la nota:", notaFinal);
            }

            db.query(getMaxInstanciaQuery, [idComensal, idProducto, notaFinal], (err, result) => {
                if (err) {
                    console.error("❌ Error al obtener la instancia:", err);
                    return res.status(500).json({ error: err.message });
                }

                // Si no hay registros con la misma nota, se asigna la instancia 1, de lo contrario se incrementa
                let nuevaInstancia = 1;
                if (result[0] && result[0].maxInstancia !== null) {
                    nuevaInstancia = result[0].maxInstancia + 1;
                }


                db.query(
                    `INSERT INTO comensal_producto (IDComensal, IDProducto, Cantidad, Entregado, Notas, Instancia)
                     VALUES (?, ?, ?, ?, ?, ?)
                     ON DUPLICATE KEY UPDATE
                        Cantidad = Cantidad + VALUES(Cantidad)`,
                    [idComensal, idProducto, cantidad, entregado, notaFinal, nuevaInstancia],
                    (err, results) => {
                        if (err) {
                            console.error("❌ Error al insertar producto:", err);
                            return res.status(500).json({ error: err.message });
                        }
                        console.log("✅ Producto insertado correctamente con nota:", notaFinal, "e instancia:", nuevaInstancia);
                        res.json({ message: "Producto insertado", nota: notaFinal, instancia: nuevaInstancia });
                    }
                );
            });
        }
    );
});
*/
// INSERT producto
app.post("/agregarProducto/:idComensal/:idProducto/:cantidad/:entregado/:notas", (req, res) => {
    const idComensal = parseInt(req.params.idComensal);
    const idProducto = parseInt(req.params.idProducto);
    const cantidad = parseInt(req.params.cantidad);
    const entregado = req.params.entregado === 'true';
    let notas = decodeURIComponent(req.params.notas);
    const extras = req.body || [];

    if (!notas || notas.toLowerCase() === "null" || notas.trim() === "") {
            notas = " ";
        }

    // console.log("📥 Datos recibidos:", { idComensal, idProducto, cantidad, entregado, notas, extras });
    const rondaRegex = /Ronda\s+(\d+)/i;
    const notasBase = notas.trim();

    // Buscar si existe un producto con la misma nota base
    const buscarNotasQuery = `
        SELECT Notas, Entregado, Instancia
        FROM comensal_producto
        WHERE IDComensal = ? AND IDProducto = ?
    `;

    db.query(buscarNotasQuery, [idComensal, idProducto], (err, results) => {
        if (err) {
            console.error(`[${getTimestamp()}] ❌ Error al buscar instancias previas:`, err);
            return res.status(500).json({ error: err.message });
        }

        let notaFinal = notas;
        let maxRonda = 1;
        let seDebeAumentarRonda = false;

        const notasFiltradas = results.filter(row => {
            const notaSinRonda = row.Notas?.replace(rondaRegex, '').trim() || "";
            return notaSinRonda === notasBase;
        });

        notasFiltradas.forEach(row => {
            const nota = row.Notas?.trim() || "";
            const entregadoPrevio = !!row.Entregado;
            console.log("Entregado previo",entregadoPrevio)
            const match = nota.match(rondaRegex);
            console.log("Nota match", match);

            if (match) {
                notaFinal = match ? match[0] : notaFinal;
                const nro = parseInt(match[1]);
                if (nro > maxRonda) {
                    maxRonda = nro;

                    if (entregadoPrevio) {
                        seDebeAumentarRonda = true;
                    }

                }
            }
            console.log(entregadoPrevio)
            if (entregadoPrevio ) {
                seDebeAumentarRonda = true;
            }
            else{
                seDebeAumentarRonda = false;
            }

        });

        console.log("Se debe aumentar ronda", seDebeAumentarRonda);
        console.log("Notas filtradas", notasFiltradas);
        console.log("Nota Final: ", notaFinal)

        if (notasFiltradas.length === 0) {
            notaFinal = notas;
            console.log(`[${getTimestamp()}] 🟢 Primera vez con esta nota. Se inserta como está:`, notaFinal);

        } else if (seDebeAumentarRonda) {
            const nuevaRonda = `Ronda ${maxRonda + 1}`;
            notaFinal = notas.trim()
                ? `${nuevaRonda} - ${notas}`
                : nuevaRonda;
            console.log(`[${getTimestamp()}] 📝 Ronda incrementada. Nota final:`, notaFinal);
        } else {
            console.log(`[${getTimestamp()}] ⏸️ Nota repetida no entregada. Se mantiene la nota:`, notaFinal);
        }

        // Ahora comparar instancias con la notaFinal correcta
        const buscarQuery = `
            SELECT Instancia
            FROM comensal_producto
            WHERE IDComensal = ? AND IDProducto = ? AND Notas = ?
        `;

        db.query(buscarQuery, [idComensal, idProducto, notaFinal], (err, results) => {
            if (err) {
                console.error(`[${getTimestamp()}] ❌ Error al buscar instancias previas:`, err);
                return res.status(500).json({ error: err.message });
            }

            const revisarInstancia = (index) => {
                if (index >= results.length) {
                    console.log(`[${getTimestamp()}] ⚠️ No se encontró una instancia coincidente. Creando nueva instancia...`);
                    crearNuevaInstancia();
                    return;
                }

                const instancia = results[index].Instancia;

                const queryExtras = `
                    SELECT IDExtra, Cantidad
                    FROM comensal_producto_extra
                    WHERE IDComensal = ? AND IDProducto = ? AND Instancia = ?
                    ORDER BY IDExtra
                `;

                db.query(queryExtras, [idComensal, idProducto, instancia], (err, extrasExistentes) => {
                    if (err) {
                        console.error(`[${getTimestamp()}] ❌ Error al consultar extras:`, err);
                        return res.status(500).json({ error: err.message });
                    }

                    const normalizarExistentes = (list) =>
                        list.map((e) => `${e.IDExtra}:${e.Cantidad}`).sort().join(",");

                    const normalizarNuevos = (list) =>
                        list.map((e) => `${e.idExtra}:${e.cantidad}`).sort().join(",");

                    const existentes = normalizarExistentes(extrasExistentes);
                    const nuevos = normalizarNuevos(extras);

                    if (existentes === nuevos) {
                        console.log(`[${getTimestamp()}] ✅ Coincidencia encontrada en instancia ${instancia}. Actualizando cantidad...`);
                        const updateQuery = `
                            UPDATE comensal_producto
                            SET Cantidad = Cantidad + ?
                            WHERE IDComensal = ? AND IDProducto = ? AND Notas = ? AND Instancia = ?
                        `;
                        db.query(updateQuery, [cantidad, idComensal, idProducto, notaFinal, instancia], (err) => {
                            if (err) {
                                console.error(`[${getTimestamp()}] ❌ Error al actualizar cantidad:`, err);
                                return res.status(500).json({ error: err.message });
                            }
                            console.log(`[${getTimestamp()}] ✅ Cantidad actualizada correctamente en instancia ${instancia}.`);
                            return res.json({ message: "Cantidad actualizada", instancia });
                        });
                    } else {
                        revisarInstancia(index + 1);
                    }
                });
            };

            const crearNuevaInstancia = () => {
                const maxQuery = `
                    SELECT MAX(Instancia) AS maxInstancia
                    FROM comensal_producto
                    WHERE IDComensal = ? AND IDProducto = ?
                `;

                db.query(maxQuery, [idComensal, idProducto], (err, rows) => {
                    if (err) {
                        console.error(`[${getTimestamp()}] ❌ Error al obtener max instancia:`, err);
                        return res.status(500).json({ error: err.message });
                    }

                    const nuevaInstancia = (rows[0]?.maxInstancia || 0) + 1;
                    console.log(`[${getTimestamp()}] 🆕 Creando nueva instancia: ${nuevaInstancia}`);

                    const insertProducto = `
                        INSERT INTO comensal_producto (IDComensal, IDProducto, Cantidad, Entregado, Notas, Instancia)
                        VALUES (?, ?, ?, ?, ?, ?)
                    `;

                    db.query(insertProducto, [idComensal, idProducto, cantidad, entregado, notaFinal, nuevaInstancia], (err) => {
                        if (err) {
                            console.error(`[${getTimestamp()}] ❌ Error al insertar nuevo producto:`, err);
                            return res.status(500).json({ error: err.message });
                        }

                        console.log(`[${getTimestamp()}] ✅ Producto insertado correctamente con instancia ${nuevaInstancia}.`);

                        if (extras.length === 0) {
                            console.log(`[${getTimestamp()}] ℹ️ No hay extras para insertar.`);
                            return res.json({ message: "Producto insertado", instancia: nuevaInstancia });
                        }

                        const insertExtras = extras.map((e) => [
                            idComensal,
                            idProducto,
                            e.idExtra,
                            e.cantidad,
                            nuevaInstancia
                        ]);

                        const placeholders = insertExtras.map(() => "(?, ?, ?, ?, ?)").join(", ");
                        const flattenedExtras = insertExtras.flat();

                        const insertExtrasQuery = `
                            INSERT INTO comensal_producto_extra (IDComensal, IDProducto, IDExtra, Cantidad, Instancia)
                            VALUES ${placeholders}
                            ON DUPLICATE KEY UPDATE Cantidad = Cantidad + VALUES(Cantidad)
                        `;

                        db.query(insertExtrasQuery, flattenedExtras, (err) => {
                            if (err) {
                                console.error(`[${getTimestamp()}] ❌ Error al insertar extras:`, err);
                                return res.status(500).json({ error: err.message });
                            }

                            console.log(`[${getTimestamp()}] ✅ Producto y extras insertados correctamente con instancia ${nuevaInstancia}.`);
                            return res.json({ message: "Producto y extras insertados", instancia: nuevaInstancia });
                        });
                    });
                });
            };

            revisarInstancia(0);
        });
    });
});

//INSERT extra
app.post("/agregarExtra/:idComensal/:idProducto/:instancia/:idExtra/:cantidad", (req, res) => {
    const idComensal = req.params.idComensal;
    const idProducto = req.params.idProducto;
    const instancia = parseInt(req.params.instancia);
    const idExtra = req.params.idExtra;
    const cantidad = parseInt(req.params.cantidad);

    console.log(`[${getTimestamp()}] 📥 Datos recibidos para agregar extra:`, {
        idComensal,
        idProducto,
        instancia,
        idExtra,
        cantidad
    });

    // Validar que exista ese producto con esa instancia
    const checkQuery = `
        SELECT * FROM comensal_producto
        WHERE IDComensal = ? AND IDProducto = ? AND Instancia = ?
    `;

    db.query(checkQuery, [idComensal, idProducto, instancia], (checkErr, rows) => {
        if (checkErr) {
            console.error(`[${getTimestamp()}] ❌ Error al verificar existencia en comensal_producto:`, checkErr);
            return res.status(500).json({ error: checkErr.message });
        }

        if (rows.length === 0) {
            console.warn(`[${getTimestamp()}] ⚠️ No se encontró la instancia especificada en comensal_producto:`, {
                idComensal,
                idProducto,
                instancia
            });
            return res.status(400).json({
                error: "No existe la instancia de comensal_producto especificada. No se puede agregar el extra."
            });
        }

        // Proceed with inserting into comensal_producto_extra
        const insertQuery = `
            INSERT INTO comensal_producto_extra (IDComensal, IDProducto, IDExtra, Cantidad, Instancia)
            VALUES (?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE Cantidad = Cantidad + VALUES(Cantidad)
        `;

        const insertParams = [idComensal, idProducto, idExtra, cantidad, instancia];

        db.query(insertQuery, insertParams, (err, results) => {
            if (err) {
                console.error(`[${getTimestamp()}] ❌ Error al insertar extra en comensal_producto_extra:`, err);
                return res.status(500).json({ error: err.message });
            }

            console.log(`[${getTimestamp()}] ✅ Extra agregado correctamente:`, {
                idComensal,
                idProducto,
                instancia,
                idExtra,
                cantidad
            });
            res.json({ message: "Extra agregado correctamente", results });
        });
    });
});

// DELETE para eliminar el producto de un comensal
app.delete("/eliminarProducto/:idProducto/:idComensal/:instancia", (req, res) => {
    const idProducto = req.params.idProducto;
    const idComensal = req.params.idComensal;
    const instancia = parseInt(req.params.instancia);



    const deleteExtrasQuery = "DELETE FROM comensal_producto_extra WHERE IDComensal = ? AND IDProducto = ? AND Instancia = ?";
    const deleteProductoQuery = "DELETE FROM comensal_producto WHERE IDComensal = ? AND IDProducto = ? AND Instancia = ?";

    db.query(deleteExtrasQuery, [idComensal, idProducto, instancia], (err, result) => {
        if (err) {
            console.error(`[${getTimestamp()}] ❌ Error al eliminar extras:`, err.message);
            return res.status(500).json({ error: err.message });
        }

        console.log(`[${getTimestamp()}] ✅ Extras eliminados: ${result.affectedRows} fila(s)`);

        db.query(deleteProductoQuery, [idComensal, idProducto, instancia], (err, result) => {
            if (err) {
                console.error(`[${getTimestamp()}] ❌ Error al eliminar producto:`, err.message);
                return res.status(500).json({ error: err.message });
            }

            console.log(`[${getTimestamp()}] ✅ Producto eliminado: ${result.affectedRows} fila(s)`);
            res.json({ message: "Producto eliminado" });
        });
    });
});


// DELETE para eliminar un producto de un comensal con cantidad
app.delete("/eliminarProductoConCantidad/:idProducto/:idComensal/:notas/:instancia/:cantidad", (req, res) => {
    const { idProducto, idComensal, notas, instancia, cantidad } = req.params;
    const instanciaNum = parseInt(instancia);
    const cantidadNum = parseInt(cantidad);
    const notasDecodificadas = decodeURIComponent(notas);

    console.log(`[${getTimestamp()}] 🗑️ Intentando eliminar producto con cantidad:`);
    console.log("🆔 IDProducto:", idProducto);
    console.log("🆔 IDComensal:", idComensal);
    console.log("📄 Notas:", notasDecodificadas);
    console.log("🔁 Instancia:", instanciaNum);
    console.log("➖ Cantidad a restar:", cantidadNum);

    // Paso 1: Restar cantidad
    db.query(
        "UPDATE comensal_producto SET Cantidad = Cantidad - ? WHERE IDComensal = ? AND IDProducto = ? AND Notas = ? AND Instancia = ?",
        [cantidadNum, idComensal, idProducto, notasDecodificadas, instanciaNum],
        (err, result) => {
            if (err) {
                console.error(`[${getTimestamp()}] ❌ Error al restar la cantidad:`, err);
                return res.status(500).json({ error: err.message });
            }

            if (result.affectedRows === 0) {
                console.warn(`[${getTimestamp()}]⚠️ No se encontró el producto con esas claves.`);
                return res.status(404).json({ error: "Producto no encontrado." });
            }

            // Paso 2: Verificar cantidad actual
            db.query(
                "SELECT Cantidad FROM comensal_producto WHERE IDComensal = ? AND IDProducto = ? AND Notas = ? AND Instancia = ?",
                [idComensal, idProducto, notasDecodificadas, instanciaNum],
                (err2, resultCantidad) => {
                    if (err2) {
                        console.error(`[${getTimestamp()}] ❌ Error al verificar cantidad:`, err2);
                        return res.status(500).json({ error: err2.message });
                    }

                    const cantidadActual = resultCantidad[0]?.Cantidad;

                    if (cantidadActual > 0) {
                        return res.json({
                            message: "Cantidad actualizada correctamente",
                            actualizado: result.affectedRows,
                            cantidadActual
                        });
                    }

                    // Paso 3: Eliminar extras primero
                    db.query(
                        "DELETE FROM comensal_producto_extra WHERE IDComensal = ? AND IDProducto = ? AND Notas = ? AND Instancia = ?",
                        [idComensal, idProducto, notasDecodificadas, instanciaNum],
                        (err3, result3) => {
                            if (err3) {
                                console.error(`[${getTimestamp()}] ❌ Error al eliminar extras:`, err3);
                                return res.status(500).json({ error: err3.message });
                            }

                            // Paso 4: Eliminar producto
                            db.query(
                                "DELETE FROM comensal_producto WHERE IDComensal = ? AND IDProducto = ? AND Notas = ? AND Instancia = ?",
                                [idComensal, idProducto, notasDecodificadas, instanciaNum],
                                (err4, result4) => {
                                    if (err4) {
                                        console.error("❌ Error al eliminar producto:", err4);
                                        return res.status(500).json({ error: err4.message });
                                    }

                                    console.log(`[${getTimestamp()}] ✅ Producto y extras eliminados con éxito.`);
                                    res.json({
                                        message: "Producto y extras eliminados correctamente",
                                        actualizado: result.affectedRows,
                                        extrasEliminados: result3.affectedRows,
                                        productoEliminado: result4.affectedRows
                                    });
                                }
                            );
                        }
                    );
                }
            );
        }
    );
});

// DELETE para eliminar a un comensal
app.delete("/eliminarComensal/:idComensal", (req, res) => {
    const idComensal = req.params.idComensal;

    const queryExtras = "DELETE FROM comensal_producto_extra WHERE IDComensal = ?";
    const queryProductos = "DELETE FROM comensal_producto WHERE IDComensal = ?";
    const queryComensal = "DELETE FROM comensal WHERE IDComensal = ?";

    db.query(queryExtras, [idComensal], (err, result) => {
        if (err) {
            console.error(`[${getTimestamp()}] ❌ Error al eliminar extras del comensal:`, err.message);
            return res.status(500).json({ error: err.message });
        }

        db.query(queryProductos, [idComensal], (err, result) => {
            if (err) {
                console.error(`[${getTimestamp()}] ❌ Error al eliminar productos del comensal:`, err.message);
                return res.status(500).json({ error: err.message });
            }

            db.query(queryComensal, [idComensal], (err, result) => {
                if (err) {
                    console.error(`[${getTimestamp()}] ❌ Error al eliminar comensal:`, err.message);
                    return res.status(500).json({ error: err.message });
                }

                console.log(`[${getTimestamp()}] ✅ Comensal eliminado ${idComensal}.`);
                res.json({ message: "Comensal eliminado" });
            });
        });
    });
});

app.put("/pagarComensal/:idComensal" , (req, res) => {
    const idComensal = req.params.idComensal;
    db.query("UPDATE comensal SET Pagado = 1 WHERE IDComensal = ?", [idComensal], (err, result) => {
        if (err) {
            console.error(`[${getTimestamp()}] ❌ Error al marcar comensal como pagado:`, err.message);
            return res.status(500).json({ error: err.message });
        }
        res.json({ message: "Comensal pagado" });
    });
});

// Delete para marcar una mesa como pagada
app.delete('/pagarMesa/:idMesa', (req, res) => {
    const idMesa = req.params.idMesa;

    const queryExtrasComensal = `DELETE FROM comensal_producto_extra
    WHERE IDComensal IN (SELECT IDComensal FROM comensal WHERE IDMesa = ?)`;

    const queryProductosComensal = `DELETE FROM comensal_producto
    WHERE IDComensal IN (SELECT IDComensal FROM comensal WHERE IDMesa = ?)`;

    const queryComensales = "Delete from comensal where IDMesa = ?";

    db.query(queryExtrasComensal, [idMesa], (err, result) => {
        if (err) {
            console.error(`[${getTimestamp()}] ❌ Error al eliminar extras de los comensales:`, err.message);
            return res.status(500).json({ error: err.message });
        }

        db.query(queryProductosComensal, [idMesa], (err, result) => {
            if (err) {
                console.error(`[${getTimestamp()}] ❌ Error al eliminar productos de los comensales:`, err.message);
                return res.status(500).json({ error: err.message });
            }

            db.query(queryComensales, [idMesa], (err, result) => {
                if (err) {
                    console.error(`[${getTimestamp()}] ❌ Error al eliminar comensales:`, err.message);
                    return res.status(500).json({ error: err.message });
                }

                console.log(`[${getTimestamp()}] ✅ Comensales pagados en la mesa ${idMesa}.`);
                res.json({ message: "Mesa pagada" });
            });
        });
    });
});

app.post('/actualizar_entregado/:id_comensal/:id_producto/:notas/:instancia/:entregado', (req, res) => {
    const { id_comensal, id_producto, notas, instancia, entregado } = req.params;

    const decodedNotas = decodeURIComponent(notas);
    const finalNotas = decodedNotas === 'sin-nota' ? " " : decodedNotas;
    const entregadoBool = entregado === 'true';


    const selectQuery = `
        SELECT * FROM comensal_producto
        WHERE IDComensal = ? AND IDProducto = ? AND Notas = ? AND instancia = ?
    `;
    const selectParams = [id_comensal, id_producto, finalNotas, instancia];



    db.query(selectQuery, selectParams, (err, results) => {
        if (err) {
            console.error(`[${getTimestamp()}] ❌ Error al buscar el registro:`, err);
            return res.status(500).json({ error: 'Error al buscar el registro', details: err.message });
        }


        if (results.length === 0) {
            console.warn(`[${getTimestamp()}] ⚠️ No se encontró ningún registro con esos parámetros:`, {
                IDComensal: id_comensal,
                IDProducto: id_producto,
                Notas: finalNotas,
                Instancia: instancia
            });
            return res.status(404).json({ error: 'Registro no encontrado' });
        }

        const updateQuery = `
            UPDATE comensal_producto
            SET entregado = ?
            WHERE IDComensal = ? AND IDProducto = ? AND Notas = ? AND Instancia = ?
        `;
        const updateParams = [entregadoBool, id_comensal, id_producto, finalNotas, instancia];



        db.query(updateQuery, updateParams, (err2, updateResult) => {
            if (err2) {
                console.error(`[${getTimestamp()}] ❌ Error al actualizar el registro:`, err2);
                return res.status(500).json({ error: 'Error al actualizar el registro', details: err2.message });
            }

            console.log(`[${getTimestamp()}] ✅ Registro actualizado correctamente:`, {
                affectedRows: updateResult.affectedRows
            });

            if (updateResult.affectedRows === 0) {
                console.warn(`[${getTimestamp()}] ⚠️ El UPDATE no afectó ninguna fila. Puede que los datos no hayan coincidido exactamente.`);
                return res.status(400).json({ warning: 'Ninguna fila fue actualizada. Verifica los datos proporcionados.' });
            }

            res.json({ success: true });
        });
    });
});

app.use((req, res, next) => {
    console.log(`📥 ${req.method} ${req.originalUrl}`);
    next();
});

// ✅ Start the server
const PORT = process.env.PORT || 5000;
app.listen(PORT, () => console.log(`[${getTimestamp()}] 🚀 Server running on port ${PORT}`));

// Generar timestamp para logs
function getTimestamp() {
    const now = new Date();
    const pad = (n) => n.toString().padStart(2, '0');
    const hours = pad(now.getHours());
    const minutes = pad(now.getMinutes());
    const day = pad(now.getDate());
    const month = pad(now.getMonth() + 1);
    const year = now.getFullYear();
    return `${hours}:${minutes} ${day}-${month}-${year}`;
}
