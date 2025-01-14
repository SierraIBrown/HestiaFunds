document.addEventListener("DOMContentLoaded", () => {
    const API_BASE_URL = "http://localhost:8080/api";

    const calendarGrid = document.getElementById("calendar-grid");
    const monthYearHeader = document.getElementById("month-year");
    const prevMonthButton = document.getElementById("prev-month");
    const nextMonthButton = document.getElementById("next-month");

    let currentDate = new Date();


    //Fetch transaction for the current month and display them on the calendar
    async function fetchTransactionsForMonth(year, month){
        try{
            const response = await fetch(`${API_BASE_URL}/transactions?year=${year}&month=${month + 1}`);
            const transactions = await response.json();
            populateCalendar(year, month, transactions);
            showNotification("Transactions for the month loaded.", "info");
        }
        catch(error){
            console.error("Error fetching transactions:", error);
            showNotification("Error fetching transactions.", "error");
        }
    }

    //Populate the calendar grid
    function populateCalendar(year, month, transactions){
        calendarGrid.innerHTML = ''; //Clear the previous calendar

        const firstDay = new Date(year, month, 1).getDay();
        const daysInMonth = new Date(year, month + 1, 0).getDate();

        //Add empty blocks for days before the first of the month
        for(let i = 0; i < firstDay; i++){
            const emptyBlock = document.createElement("div");
            emptyBlock.className = "calendar-day empty";
            calendarGrid.appendChild(emptyBlock);
        }

        //Add day blocks with transactions
        for(let day = 1; day <= daysInMonth; day++){
            const dayBlock = document.createElement("div");
            dayBlock.className = "calendar-day";
            dayBlock.innerHTML = `<div class="day-number">${day}</div>`;

            const dayTransactions = transactions.filter((transaction) => {
                const transactionDate = new Date(transaction.date);
                return(
                    transactionDate.getUTCFullYear() === year &&
                    transactionDate.getUTCMonth() === month &&
                    transactionDate.getUTCDate() === day
                );
            });

            dayTransactions.forEach((transaction) => {
                const transactionDiv = document.createElement("div");
                transactionDiv.className = "transaction";

                //Create the category tag
                const categoryTag = document.createElement("span");
                categoryTag.className = "category-tag";
                categoryTag.textContent = transaction.category.name;
                categoryTag.style.backgroundColor = transaction.category.color || "#ddd";

                //Create the transaction amount element
                const transactionAmount = document.createElement("span");
                transactionAmount.className = "transaction-amount";
                transactionAmount.textContent = `$${transaction.amount.toFixed(2)}`;

                transactionDiv.appendChild(categoryTag);
                transactionDiv.appendChild(transactionAmount);
                dayBlock.appendChild(transactionDiv);
            });


            //Add the "+" button to each day
            const addButton = document.createElement("button");
            addButton.className = "add-transaction-btn";
            addButton.textContent = "+";
            addButton.onclick = () => showTransactionForm(day, year, month);
            dayBlock.appendChild(addButton);

            calendarGrid.appendChild(dayBlock);
        }

        //Update month and year header
        monthYearHeader.textContent = `${currentDate.toLocaleString("default", {
            month: "long",
        })} ${currentDate.getFullYear()}`;
    }

    //Show transaction form modal
    function showTransactionForm(day, year, month){
        showModal(
            "Add New Transaction",
            `
            <form id="modal-transaction-form">
                <label for="modal-transaction-date">Date:</label>
                <input type="date" id="modal-transaction-date" value="${year}-${String(month + 1).padStart(2, "0")}-${String(day).padStart(2, "0")}" readonly required>
                <label for="modal-transaction-amount">Amount:</label>
                <input type="number" id="modal-transaction-amount" step="0.01" required>
                <label for="modal-transaction-category">Category:</label>
                <select id="modal-transaction-category" required>
                    <option value="" disabled selected>Select a category</option>
                </select>
                <label for="modal-transaction-description">Description:</label>
                <input type="text" id="modal-transaction-description" required>
            </form>
            `,
            async () => {
                const date = document.getElementById("modal-transaction-date").value;
                const amount = parseFloat(document.getElementById("modal-transaction-amount").value);
                const category = parseInt(document.getElementById("modal-transaction-category").value);
                const description = document.getElementById("modal-transaction-description").value;

                if(!amount || !category || !description){
                    showNotification("All fields are required.", "warning");
                    return;
                }

                const transaction = { date, amount, category: { id: category }, description };

                try {
                    const response = await fetch(`${API_BASE_URL}/transactions`, {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json",
                        },
                        body: JSON.stringify(transaction),
                    });
        
                    if (response.ok) {
                        showNotification("Transaction added successfully!", "success");
                        fetchTransactionsForMonth(currentDate.getFullYear(), currentDate.getMonth());
                    } else {
                        showNotification("Failed to add transaction.", "error");
                    }
                } 
                catch (error) {
                    console.error("Error adding transaction:", error);
                    showNotification("Error adding transaction.", "error");
                }
            },
            "add"
        );

        fetchCategoriesForTransactionForm();
    }

    //Fetch categories for the transaction form
    async function fetchCategoriesForTransactionForm(){
        try{
            const response = await fetch(`${API_BASE_URL}/categories`);
            const categories = await response.json();

            const categorySelect = document.getElementById("modal-transaction-category");
            categorySelect.innerHTML = "";

            categories.forEach((category) => {
                const option = document.createElement("option");
                option.value = category.id;
                option.textContent = category.name;
                categorySelect.appendChild(option);
            });
        }
        catch(error){
            console.error("Error fetching categories:", error);
            showNotification("Error fetching categories.", "error");
        }
    }
    

    //Handle month navigation
    prevMonthButton.addEventListener("click", () => {
        currentDate.setMonth(currentDate.getMonth() - 1);
        fetchTransactionsForMonth(currentDate.getFullYear(), currentDate.getMonth());
    });

    nextMonthButton.addEventListener("click", () => {
        currentDate.setMonth(currentDate.getMonth() + 1);
        fetchTransactionsForMonth(currentDate.getFullYear(), currentDate.getMonth());
    });

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

    //Initial fetches
    fetchTransactionsForMonth(currentDate.getFullYear(), currentDate.getMonth());
});