function submit(storeID) {
    document.location.href = `/ajouterUnMembre?url=${document.location.pathname}&username=${input.value}&store=${storeID}`
}

function updateButton(userID, boutiqueID) {
    let url = document.location.pathname
    let roleID = document.getElementById(userID).value
    document.location.href = `/editMember?url=${url}&roleID=${roleID}&userID=${userID}&boutiqueID=${boutiqueID}`
}

function deleteButton(userID, boutiqueID) {
    let url = document.location.pathname
    document.location.href = `/removeMember?url=${url}&userID=${userID}&boutiqueID=${boutiqueID}`
}

function modifySalary(boutiqueID,userID,salary) {
    let url = document.location.pathname
    document.location.href = `/editSalary?url=${url}&salary=${salary}&boutiqueID=${boutiqueID}&userID=${userID}`
}