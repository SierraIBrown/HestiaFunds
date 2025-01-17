document.addEventListener("DOMContentLoaded", () => {
    const API_BASE_URL = "http://localhost:8080/api";

    const budgetsTableBody = document.getElementById("budget-table-body");
    const viewEditCategoriesButton = document.getElementById("view-edit-categories");
    const backToTransactionsButton = document.getElementById("back-to-transactions");

    let categories = [];
    let budgets = [];

    //Fetch categories and budgets
    async function fetchCategoriesAndBudgets(){
        try{
            const [categoriesResponse, budgetsResponse] = await Promise.all([
                fetch(`${API_BASE_URL}/categories`),
                fetch(`${API_BASE_URL}/budgets`)
            ]);

            categories = await categoriesResponse.json();
            budgets = await budgetsResponse.json();

            populateBudgetsTable();
            showNotification("Categories and Budgets loaded successfully!", "success");
        }
        catch(error){
            console.error("Error fetching categories or budgets:", error);
            showNotification("Error fetching categories and/or budgets", "error");
        }
    }

    //Populate budgets table
    function populateBudgetsTable(){
        budgetsTableBody.innerHTML = "";
    
        budgets
            .sort((a, b) => a.category.name.localeCompare(b.category.name))
            .forEach(budget => {
                const row = document.createElement("tr");
    
                //Category tag
                const categoryCell = document.createElement("td");
                categoryCell.textContent = budget.category.name;
                row.appendChild(categoryCell);
    
                const budgetCell = document.createElement("td");
                budgetCell.textContent = `$${budget.amount.toFixed(2)}`;
                row.appendChild(budgetCell);
    
                budgetsTableBody.appendChild(row);
            });
    }

    function showAddBudgetModal(){
        showModal(
            "Add a Budget",
            `
            <form id="modal-budget-form">
                <div id="modal-category-tags-container">
                <label for="modal-budget-category">Category:</label>
                <div id="modal-category-tags"></div>
                </div>
                <input type="hidden" id="modal-budget-category" required>
                <label for="modal-budget-amount">Budget Amount:</label>
                <input type="number" id="modal-budget-amount" step="0.01" required>
            </form>
            `,
            async() => {
                const categoryId = parseInt(document.getElementById("modal-budget-category").value);
                const amount = parseFloat(document.getElementById("modal-budget-amount").value);

                if(!categoryId || !amount){
                    showNotification("All fields are required.", "warning");
                    return;
                }

                const budgetData = {
                    category: { id: parseInt(categoryId) },
                    amount,
                    periodStart: "2025-01-01",
                    periodEnd: "2025-01-31",
                };

                try{
                    const response = await fetch(`${API_BASE_URL}/budgets`, {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json"
                        },
                        body: JSON.stringify(budgetData),
                    });

                    if(response.ok){
                        showNotification("Budget added successfully!", "success");
                        fetchCategoriesAndBudgets();
                    }
                    else{
                        showNotification("Failed to add budget.", "error");
                    }
                }
                catch(error){
                    console.error("Error adding budget:", error);
                    showNotification("Error adding budget.", "error");
                }
            },
            "add"
        );

        fetchCategoriesForBudgetForm("modal-category-tags", "modal-budget-category");
    }

    //Fetch cateogries for the modal
    async function fetchCategoriesForBudgetForm(containerId, hiddenInputId){
        const categoryContainer = document.getElementById(containerId);
        const hiddenCategoryInput = document.getElementById(hiddenInputId);

        if(!categoryContainer || !hiddenCategoryInput){
            console.error(`Container or input element not found: ${containerId}, ${hiddenInputId}`);
            return;
        }

        categoryContainer.innerHTML = "";

        categories.forEach((category) => {
            const tag = document.createElement("span");
            tag.className = "category-tag selectable";
            tag.textContent = category.name;
            tag.style.backgroundColor = category.color || "#ddd";
            tag.dataset.categoryId = category.id;

            tag.addEventListener("click", () => {
                document
                    .querySelectorAll(".category-tag.selectable")
                    .forEach((el) => el.classList.remove("selected"));
                tag.classList.add("selected");
                hiddenCategoryInput.value = category.id;
            });

            categoryContainer.appendChild(tag);
        });

        //Add a view/edit categories button
        const viewEditButton = document.createElement("button");
        viewEditButton.className = "view-edit-categories-btn";
        viewEditButton.textContent = "View/Edit Categories";
        viewEditButton.type = "button";
        viewEditButton.onclick = () => {
            const modalState = {
                amount: document.getElementById("modal-transaction-amount")?.value || "",
                date: document.getElementById("modal-transaction-date")?.value || "",
                description: document.getElementById("modal-transaction-description")?.value || "",
                category: document.getElementById("modal-transaction-category")?.value || "",
            };
            sessionStorage.setItem("modalState", JSON.stringify(modalState));
        
            window.location.href = "../html/categories.html";
        };
        
        categoryContainer.appendChild(viewEditButton);
    }

    //Show a modal
    function showModal(title, content, onConfirm, type = "info"){
        //Create the modal overlay
        const modalOverlay = document.createElement("div");
        modalOverlay.className = "modal-overlay";
        
        //Create the modal container
        const modal = document.createElement("div");
        modal.className = "modal ${type}";
        
        //Create the modal title
        const modalTitle = document.createElement("h3");
        modalTitle.textContent = title;
        modal.appendChild(modalTitle);
        
        //Add the modal content
        const modalContent = document.createElement("div");
        modalContent.className = "modal-content";
        modalContent.innerHTML = content;
        modal.appendChild(modalContent);
        
        //Add action buttons
        const actions = document.createElement("div");
        actions.className = "modal-actions";
        
        const confirmButton = document.createElement("button");
        confirmButton.textContent = "Confirm";
        confirmButton.className = "modal-confirm-btn";
        confirmButton.onclick = () => {
            onConfirm();
            modalOverlay.remove();
        };
        
        const cancelButton = document.createElement("button");
        cancelButton.textContent = "Cancel";
        cancelButton.className = "modal-cancel-btn";
        cancelButton.onclick = () => modalOverlay.remove();
        
        actions.appendChild(cancelButton);
        actions.appendChild(confirmButton);
        modal.appendChild(actions);
    
        modalOverlay.appendChild(modal);
        document.body.appendChild(modalOverlay);
    }

    //Notification
    function showNotification(message, type = "success"){
        const notificationContainer = document.getElementById("notification-container");
    
        //Create the notification element
        const notification = document.createElement("div");
        notification.className = `notification ${type}`;
        notification.textContent = message;
    
        //Add a close button
        const closeButton = document.createElement("button");
        closeButton.innerHTML = "&times;"
        closeButton.onclick = () => notification.remove();
        notification.appendChild(closeButton);
    
        //Append it to the container
        notificationContainer.appendChild(notification);
    
        //Remove it after 4 seconds
        setTimeout(() => {
            notification.remove();
        }, 4000);
    }

    //Event listener for add button
    const addBudgetButton = document.getElementById("add-budget");
    addBudgetButton.addEventListener("click", showAddBudgetModal);

    viewEditCategoriesButton.addEventListener("click", () => {
        window.location.href = "../html/categories.html";
    });

    backToTransactionsButton.addEventListener("click", () => {
        window.location.href = "../html/transactions.html";
    });

    //Initial fetch
    fetchCategoriesAndBudgets();
});