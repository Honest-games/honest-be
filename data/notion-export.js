let res = "START TRANSACTION;"

// TRUNCATE TABLE questions; TRUNCATE TABLE levels; TRUNCATE TABLE decks;"

res += "INSERT INTO decks (id, language_code, name, description, labels, vector_image_id, hidden, promo, deck_order) VALUES ";

res += Array.from(document.querySelector('[data-block-id=de41a85a-3671-43a2-800a-1ce43ef93c59]')
    .querySelectorAll('.notion-table-view-row'))
    .map(row => {
        const cellsObjs = Array.from(row.querySelectorAll('.notion-table-view-cell'))
        const cells = cellsObjs.map(cell => cell.innerText)
        const inc = (()=>{let x=0;return ()=>x++})()
        const id = cells[inc()]
        const lang = cells[inc()]
        let name = cells[inc()]
        const desc = cells[inc()]
        const promo = cellsObjs[inc()].innerText
        const promoValue = promo ? `'${promo}'` : 'null'
        const imageId = cells[inc()]
        const labels = cellsObjs[inc()].innerText
        const order = cells[inc()]
        return `('${id}', '${lang.substring(0,2).toUpperCase()}', '${name}', '${desc}', '${labels}', '${imageId}', ${promo ? "true" : "false"}, ${promoValue}, ${order})`
    }).join(", ")

res += ` on conflict (id) do update set
        language_code = excluded.language_code,
        name = excluded.name,
        description = excluded.description,
        labels = excluded.labels,
        vector_image_id = excluded.vector_image_id,
        hidden = excluded.hidden,
        promo = excluded.promo,
        deck_order = excluded.deck_order;`

res += "INSERT INTO levels (id, deck_id, level_order, name, description, color,bg_image_id) VALUES "

res += Array.from(document.querySelectorAll('.notion-collection_view-block')[4]
    .querySelectorAll('.notion-table-view-row'))
    .map(row => {
        const cellsObjs = Array.from(row.querySelectorAll('.notion-table-view-cell'))
        const cells = cellsObjs.map(cell => cell.innerText)
        const inc = (()=>{let x=1;return ()=>x++})()
        const id = cells[inc()]
        const deckId = cells[inc()]
        let levelOrder = cells[inc()];
        let name = cells[inc()];
        const desc = cells[inc()];
        let color = cells[inc()];
        let bgImageId = cells[inc()];
        return `('${id}', '${deckId}', ${levelOrder}, '${name}', '${desc}', '${color}', ${bgImageId===""?null:`'${bgImageId}'`})`
    }).join(", ");

res += `on conflict (id) do update set
        deck_id = excluded.deck_id,
        level_order = excluded.level_order,
        name = excluded.name,
        description = excluded.description,
        color = excluded.color,
        bg_image_id = excluded.bg_image_id;`

res += "INSERT INTO questions (id, level_id, text, additional_text) VALUES ";

res += Array.from(document.querySelectorAll('.notion-collection_view-block')[8]
    .querySelectorAll('.notion-table-view-row'))
    .map(row => {
        const cellsObjs = Array.from(row.querySelectorAll('.notion-table-view-cell'))
        const cells = cellsObjs.map(cell => cell.innerText)
        const additional = cells[3]
        return `('${cells[0]}', '${cells[1]}', '${processText(cells[2])}', ${additional ? `'${additional}'` : 'null'})`;
    }).join(", ")

res += `       on conflict (id) do update set
         id = excluded.id,
         level_id = excluded.level_id,
         text = excluded.text,
         additional_text = excluded.additional_text;`

res += " COMMIT;"

function processText(text) {
    return text.replaceAll("'","''")
}