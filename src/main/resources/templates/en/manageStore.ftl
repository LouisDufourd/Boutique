<#import "header.ftl" as head>
<#import "form.ftl" as form>
<#import "navbar.ftl" as nav>
<!DOCTYPE html>
<html lang="fr">
<head>
    <@head.header>
    </@head.header>
    <title>Gestion</title>
</head>
<body>
<@nav.navbar>
</@nav.navbar>
<#switch data.connectedpage.createshoperror>
    <#case 1>
        <div class="alert alert-danger" role="alert">
            The store name is already in use
        </div>
        <#break>
    <#case 2>
        <div class="alert alert-danger" role="alert">
            The store name is invalid
        </div>
        <#break>
</#switch>
<#switch data.addmembererror>
    <#case 1>
        <div class="alert alert-danger" role="alert">
            There are no users with this name
        </div>
        <#break>
</#switch>
<div class="container">
    <ul class="nav nav-tabs">
        <li class="nav-item">
            <a class="nav-link active" data-bs-toggle="tab" href="#member">Member</a>
        </li>
        <li class="nav-item">
            <a class="nav-link" data-bs-toggle="tab" href="#stock">Stock</a>
        </li>
        <#if data.roleid == 1>
            <li class="nav-item">
                <a class="nav-link" data-bs-toggle="tab" href="#salary">Salary</a>
            </li>
        </#if>
    </ul>
    <div class="tab-content">
        <div class="tab-pane container active" id="member">
            <#switch data.roleid>
                <#case 1>
                <#case 2>
                    <table class="table">
                        <thead>
                        <tr>
                            <th scope="col">#</th>
                            <th scope="col">Username</th>
                            <th scope="col">Role</th>
                            <th scope="col">Manage</th>
                        </tr>
                        </thead>
                        <tbody>
                        <#list data.members as member>
                            <tr>
                                <th scope="row">${member_index + 1}</th>
                                <td>${member.utilisateur.username}</td>
                                <td>
                                    <#if data.roleid != 1 && member.role.id == 1>
                                        ${member.role.nomfr}
                                    <#else>
                                        <select id="${member.utilisateur.id}" class="form-select">
                                            <#list member.missingrole as role>
                                                <#if member.role.id == role.id>
                                                    <option value="${role.id}" selected>${role.nomfr}</option>
                                                <#else>
                                                    <option value="${role.id}">${role.nomfr}</option>
                                                </#if>
                                            </#list>
                                        </select>
                                    </#if>
                                </td>
                                <td>
                                    <#if member.role.id != 1>
                                        <button class="btn btn-outline-success"
                                                onclick="updateButton(${member.utilisateur.id},${data.storeid})">
                                            Edit
                                        </button>
                                        <button class="btn btn-outline-danger"
                                                onclick="deleteButton(${member.utilisateur.id},${data.storeid})">
                                            Remove
                                        </button>
                                        <script>

                                        </script>
                                    <#else>
                                        <button class="btn btn-outline-success disabled">Edit</button>
                                        <button class="btn btn-outline-danger disabled">Remove</button>
                                    </#if>
                                </td>
                            </tr>
                        </#list>
                        </tbody>
                    </table>
                    <a class="btn btn-outline-success" data-bs-toggle="modal" data-bs-target="#addUserForm"
                       href="/ajouterUnMembre">Add a member</a>
                    <#break>
                <#default>
                    <table class="table">
                        <thead>
                        <tr>
                            <th scope="col">#</th>
                            <th scope="col">Username</th>
                            <th scope="col">Role</th>
                        </tr>
                        </thead>
                        <tbody>
                        <#list data.members as member>
                            <tr>
                                <th scope="row">${member_index + 1}</th>
                                <td>${member.utilisateur.username}</td>
                                <td>${member.role.nomfr}</td>
                            </tr>
                        </#list>
                        </tbody>
                    </table>
                    <#break>
            </#switch>
        </div>
        <div class="tab-pane container fade" id="stock">
            <table class="table">
                <thead>
                <tr>
                    <th scope="col">#</th>
                    <th scope="col">Product</th>
                    <th scope="col">Quantity</th>
                    <th scope="col">Price</th>
                </tr>
                </thead>
                <tbody>
                <#list data.stocks as stock>
                    <tr>
                        <th scope="row">${stock_index + 1}</th>
                        <td>${stock.article.nom}</td>
                        <td>${stock.quantity}</td>
                        <td>${stock.price}€</td>
                    </tr>
                </#list>
                </tbody>
            </table>
        </div>
        <#if data.roleid == 1>
            <div class="tab-pane container fade" id="salary">
                <table class="table">
                    <thead>
                    <tr>
                        <th scope="col">#</th>
                        <th scope="col">Username</th>
                        <th scope="col">Salary</th>
                        <th scope="col">Manage</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#list data.members as member>
                        <tr>
                            <th scope="row">${member_index + 1}</th>
                            <td>${member.utilisateur.username}</td>
                            <td>
                                <input class="form-control" id="salary${member_index}" type="number" min="0" step="0.01" value="${member.salary}"/>
                            </td>
                            <td>
                                <button type="button" onclick="modifySalary(${member.boutique.id},${member.utilisateur.id},document.getElementById('salary${member_index}').value)" class="btn btn-outline-success">Edit</button>
                            </td>
                        </tr>
                    </#list>
                    </tbody>
                </table>
            </div>
        </#if>
    </div>
</div>

<div class="modal fade" id="addUserForm" tabindex="-1" aria-labelledby="ajouterUnMembre" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="createShopLabel">Add a user</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <div class="input-group">
                    <div id="search-autocomplete" class="form-outline">
                        <label class="form-label" for="username">Username</label>
                        <input type="text" id="username" class="form-control"/>
                        <div id="result"></div>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" onclick="submit(${data.storeid})" class="btn btn-primary">Add a user</button>
            </div>
        </div>
    </div>
</div>
<@form.formulaire>
</@form.formulaire>
<script src="/assets/js/manageStore.js"></script>
<script>
    const search_terms = JSON.parse('${data.users}')
    var input = document.getElementById('username')
    var res = document.getElementById("result")

    input.addEventListener("input", showResults)

    function autocompleteMatch(input) {
        let terms = []
        if (input === '') {
            return terms;
        }
        var reg = new RegExp(input.toLowerCase())
        search_terms.forEach(e => {
            if (e.username.toLowerCase().match(reg)) {
                terms.push(e.username)
            }
        })
        return terms
    }

    function showResults(val) {
        res.innerHTML = '';
        let list = '';
        let terms = autocompleteMatch(val.target.value);
        for (i = 0; i < terms.length; i++) {
            list += `<li class="list-group-item" style="cursor: pointer" id="` + i + `" onclick="autoComplete(this)">` + terms[i] + `</li>`;
        }
        res.innerHTML = `<ul class="list-group">` + list + `</ul>`;
    }

    function autoComplete(e) {
        input.value = e.innerHTML
    }
</script>
<script src="/assets/js/bootstrap.min.js"></script>
</body>