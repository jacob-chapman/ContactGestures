package com.jacobchapman.contactgestures

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jacobchapman.contactgestures.databinding.ContactItemBinding

class ContactsAdapter(var contacts: MutableList<ContactModel>, val onContactClickedListener: OnContactClickedListener) : RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false )
        val binding = ContactItemBinding.bind(view)
        return ContactViewHolder(binding).listen { position, _ ->
            onContactClickedListener.contactClicked(contacts[position])
        }
    }

    override fun getItemCount(): Int {
        return contacts.count()
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.bind(contact)
    }


    class ContactViewHolder(private val binding: ContactItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(contact: ContactModel) {
            binding.contact = contact
            binding.executePendingBindings()
        }
    }

}

interface OnContactClickedListener {
    fun contactClicked(contact: ContactModel)
}