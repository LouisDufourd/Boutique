function changeRating(input) {
    input.style.setProperty('--value', `${input.valueAsNumber}`)
}

function submitRating(shopID) {
    let comment = document.getElementById(`commentary${shopID}`).value
    let rating = document.getElementById(`rating${shopID}`).valueAsNumber
    console.log(`comment: ${comment}\nrating: ${rating}\nshopID: ${shopID}`)
    location.href = `/shops/${shopID}/comment/${comment}/${rating}`
}

let elements = document.getElementsByClassName("rating")
for (let i = 0; i < elements.length; i++) {
    elements.item(i).style.setProperty('--value', `${elements.item(i).valueAsNumber}`)
}