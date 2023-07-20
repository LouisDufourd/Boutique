<#import "header.ftl" as head>
<#import "connectedNavbar.ftl" as nav>
<#import "form.ftl" as form>
<!DOCTYPE html>
<html lang="fr">
<head>
    <@head.header>
    </@head.header>
    <title>Boutique</title>
</head>
<body>
<@nav.navbar>
</@nav.navbar>
<#switch data.connectedpage.createshoperror>
    <#case 1>
        <div class="alert alert-danger" role="alert">
            Le nom de la boutique est déjà utilisé
        </div>
        <#break>
    <#case 2>
        <div class="alert alert-danger" role="alert">
            Le nom de la boutique est invalide
        </div>
        <#break>
</#switch>
<div class="container">
    <div class="main-body">
        <#list data.stores as store>
            <div class="gutters-sm">
                <div class="col-md-3 mb-3">
                    <div class="card">
                        <div class="card-body">
                            <h1>${store.boutique.nom}</h1>
                            <p>Propriétaire : ${store.utilisateur.username}</p>
                            <a href="/stores/${store.boutique.id}" class="btn btn-primary">Gerer</a>
                            <#if data.roles[store_index] == 1>
                                <a href="/stores/${store.boutique.id}/delete" class="btn btn-danger">Supprimer</a>
                            </#if>
                        </div>
                    </div>
                </div>
            </div>
        </#list>
    </div>
</div>
<@form.formulaire>
</@form.formulaire>
</body>
<script src="/assets/js/bootstrap.min.js"></script>
</html>