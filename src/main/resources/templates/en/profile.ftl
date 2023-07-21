<#import "connectedNavbar.ftl" as nav>
<#import "header.ftl" as head>
<#import "form.ftl" as form>
<!DOCTYPE html>
<html lang="en">
<head>
    <@head.header>
    </@head.header>
    <style>
        .inventory-container {
            display: grid;
            grid-template-columns: repeat(9, 1fr);
            grid-gap: 5px;
            margin-top: 5px;
        }

        .item-slot {
            position: relative;
            width: 100px;
            height: 100px;
            background-color: lightgray;
            border-radius: 5px;
            overflow: hidden;
            text-align: center;
        }

        .item-image {
            width: 50px;
            height: 50px;
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
        }

        .item-displayName {
            position: absolute;
            bottom: 0;
            left: 0;
            width: 100%;
            background-color: rgba(0, 0, 0, 0.7);
            color: white;
            padding: 5px;
            font-size: 12px;
            text-align: center;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }

        .item-quantity {
            position: absolute;
            top: 0;
            right: 0;
            padding: 3px 5px;
            background-color: rgba(0, 0, 0, 0.7);
            color: white;
            font-size: 12px;
        }
    </style>
    <title>Profile</title>
</head>
<body>
<@nav.navbar>
</@nav.navbar>
<!--Gestion des erreurs des formulaires-->
<#switch data.editerror>
    <#case 1>
        <div class="alert alert-danger" role="alert">
            The username is already in use
        </div>
        <#break>
    <#case 2>
        <div class="alert alert-danger" role="alert">
            Invalid user name
        </div>
        <#break>
    <#case 3>
        <div class="alert alert-danger" role="alert">
            Invalid password
        </div>
        <#break>
</#switch>
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
<div class="container">
    <div class="main-body">
        <div class="row gutters-sm">
            <!--Information-->
            <div class="col-md-4 mb-3">
                <div class="card">
                    <div class="card-body">
                        <div class="d-flex flex-column align-items-center text-center">
                            <div class="mt-3">
                                <h2>Information</h2>
                                <p class="text-secondary mb-1">Account creation date : ${data.user.createTime}</p>
                                <p class="text-muted mb-1">Balance : ${data.user.solde}€</p>
                                <p class="text-muted font-size-sm">Number of slots : ${data.user.nombreSlot}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!--Modification du profile-->
            <div class="col-md-8">
                <form method="get" action="/editUser">
                    <div class="card mb-3">
                        <div class="card-body">
                            <div class="row">
                                <div class="col-sm-3">
                                    <label for="username">
                                        <h6 class="mb-0">Username</h6>
                                    </label>
                                </div>
                                <div class="col-sm-9 text-secondary">
                                    <input type="text" id="username" name="username" class="form-control"
                                           value="${data.user.username}" required>
                                </div>
                            </div>
                            <hr>
                            <div class="row">
                                <div class="col-sm-3">
                                    <label for="password">
                                        <h6 class="mb-0">Passwords</h6>
                                    </label>
                                </div>
                                <div class="col-sm-9 text-secondary">
                                    <input type="password" id="password" class="form-control" value="" name="password"
                                           required/>
                                </div>
                            </div>
                            <hr>
                            <div class="row">
                                <div class="col-sm-3">
                                    <label for="email">
                                        <h6 class="mb-0">Email</h6>
                                    </label>
                                </div>
                                <div class="col-sm-9 text-secondary">
                                    <input type="email" id="email" class="form-control" name="email"
                                           value="${data.user.email}" required/>
                                </div>
                            </div>
                            <hr>
                            <div class="row">
                                <div class="col-sm-12">
                                    <button type="submit" class="btn btn-primary">Save</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
            <!--Inventaire-->
            <div class="col-md-9">
                <h1>Inventory</h1>
                <#assign index = 0>
                <div class="inventory-container">
                    <#list 0..40 as slot>
                        <#assign bool=true>
                        <#list data.inventory.items as item>
                            <#if item.slot == slot>
                                <#assign bool = false>
                                <div class="item-slot">
                                    <img class="item-image" src="${item.item.imageSrc}"
                                         alt="${item.item.material.name}">
                                    <div class="item-displayName">${item.item.material.name}</div>
                                    <div class="item-quantity">x${item.item.quantity}</div>
                                </div>
                            </#if>
                        </#list>
                        <#if bool>
                            <div class="item-slot">
                                <#switch index>
                                    <#case 36>
                                        <img class="item-image" src="/assets/img/item/empty_armor_slot_boots.png"
                                             alt="">
                                        <#break>
                                    <#case 37>
                                        <img class="item-image" src="/assets/img/item/empty_armor_slot_leggings.png"
                                             alt="">
                                        <#break>
                                    <#case 38>
                                        <img class="item-image" src="/assets/img/item/empty_armor_slot_chestplate.png"
                                             alt="">
                                        <#break>
                                    <#case 39>
                                        <img class="item-image" src="/assets/img/item/empty_armor_slot_helmet.png"
                                             alt="">
                                        <#break>
                                    <#case 40>
                                        <img class="item-image" src="/assets/img/item/empty_armor_slot_shield.png"
                                             alt="">
                                        <#break>
                                </#switch>
                            </div>
                        </#if>
                        <#assign index = index + 1>
                    </#list>
                </div>
            </div>
        </div>
    </div>
</div>
<@form.formulaire>
</@form.formulaire>
<br/>
<br/>
</body>
<script src="/assets/js/bootstrap.min.js"></script>
</html>