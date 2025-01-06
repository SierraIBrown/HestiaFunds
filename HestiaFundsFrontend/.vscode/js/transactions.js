document.addEventListener("DOMContentLoaded", () => {
    const transactionsBody = document.getElementById("transactions-body");
    const categorySelect = document.getElementById("category");
    const transactionForm = document.getElementById("transaction-form");

    const API_BASE_URL = "http://localhost:8080/api";

    //Fetch transactions and populate table
    async function fetchTransactions(){
        try{
            const response = await fetch(`${API_BASE_URL}/transactions`);
            const transactions = await response.json();

            //Clear table
            transactionsBody.innerHTML = "";

            //Populate
            transactions.forEach((transaction) => {
                const row = document.createElement("tr"); // Dynamically create a new row
            
                row.innerHTML = `
                    <td>${transaction.date}</td>
                    <td>${transaction.amount.toFixed(2)}</td>
                    <td>${transaction.category.name}</td>
                    <td>${transaction.description}</td>
                    <td>
                        <button data-id="${transaction.id}" class="edit-btn">Edit</button>
                        <button data-id="${transaction.id}" class="delete-btn">Delete</button>
                    </td>
                `;
            
                transactionsBody.appendChild(row);
            });
        }
        catch(error){
            console.error("Error fetching transactions:", error);
        }
    }

    //Fetch categories
    async function fetchCategories(){
        try{
            const response = await fetch(`${API_BASE_URL}/categories`);
            const categories = await response.json();

            categories.forEach((category) => {
                const option = document.createElement("option");
                option.value = category.id;
                option.textContent = category.name;
                categorySelect.appendChild(option);
            });
        }
        catch(error){
            console.error("Error fetching categories:", error);
        }
    }

    async function editTransaction(transaction){
        try{
            const response = await fetch(`${API_BASE_URL}/transactions/${transaction.id}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(transaction),
            });

            if(response.ok){
                alert("Transaction updated successfully!");
                fetchTransactions();
            }
            else{
                alert("Failed to update transaction.");
            }
        }
        catch(error){
            console.error("Error updating transaction:", error);
        }
    }

    async function deleteTransaction(id){
        try{
            const response = await fetch(`${API_BASE_URL}/transactions/${id}`, {
                method: "DELETE",
            });

            if(response.ok){
                alert("Transaction deleted successfully!");
                fetchTransactions();
            }
            else{
                alert("Failed to delete transaction.");
            }
        }
        catch(error){
            console.error("Error deleting transaction:", error);
        }
    }

    //Handle table button clicks
    transactionsBody.addEventListener("click", async (event) => {
        const target = event.target;

        //Edit button
        if(target.classList.contains("edit-btn")){
            const id = target.dataset.id;
            const row = target.closest("tr");
            const amount = prompt("Enter new amount:", row.children[1].textContent);
            const date = prompt("Enter new date (YYYY-MM-DD):", row.children[0].textContent);
            const description = prompt("Enter new description:", row.children[3].textContent);
            const category = prompt("Enter new category ID:", row.children[2].dataset.categoryId);

            if(amount && date && description && categoryId){
                const updatedTransaction = {
                    id: id,
                    amount: parseFloat(amount),
                    date: date,
                    description: description,
                    category: { id: parseInt(categoryId) },
                };

                await editTransaction(updatedTransaction);
            }
            else{
                alert("All fields are required for editing a transaction.");
            }
        }

        //Delete button
        if(target.classList.contains("delete-btn")){
            const id = target.dataset.id;
            if(confirm("Are you sure you want to delete this transaction?")){
                await deleteTransaction(id);
            }
        }
    });

    //Function to submit a new transaction
    async function submitTransaction(event){
        event.preventDefault();

        console.log("Submitting transaction...");

        const amount = document.getElementById("amount").value;
        const date = document.getElementById("date").value;
        const description = document.getElementById("description").value;
        const categoryId = document.getElementById("category").value;

        console.log("Transaction data:", { amount, date, description, categoryId });

        //Validate inputs
        if(!amount || !date || !description || !categoryId){
            alert("All fields are required.");
            console.log("Validation failed: Missing fields");
            return;
        }

        //Create transaction object
        const transaction = {
            amount: parseFloat(amount),
            date: date,
            description: description,
            category: {
                id: parseInt(categoryId),
            },
        };

        try{
            console.log("Sending transaction to the API...");
            const response = await fetch(`${API_BASE_URL}/transactions`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(transaction),
            });

            if(response.ok){
                const result = await response.json();
                console.log("Transaction submitted successfully:", result);
                alert("Transaction submitted successfully!");
                document.getElementById("transaction-form").reset();
            }
            else{
                const errorMessage = await response.text();
                console.log("Error submitting transaction:", errorMessage);
                alert(`Failed to submit transaction: ${errorMessage}`);
            }
        }
        catch(error){
            console.error("Unexpected error submitting transaction:", error);
            alert("An unexpected error occurred. Please try again.");
        }
    }

    //Attach event listener to the form
    document.getElementById("transaction-form").addEventListener("submit", submitTransaction);

    //Initial fetches
    fetchTransactions();
    fetchCategories();
});