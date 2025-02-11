package com.siscem.portal_sae.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.siscem.portal_sae.dtos.contact.ContactCreateDTO;
import com.siscem.portal_sae.dtos.contact.ContactDTO;
import com.siscem.portal_sae.dtos.contact.ContactEditDTO;
import com.siscem.portal_sae.models.Contact;
import com.siscem.portal_sae.repositories.AccountRepository;
import com.siscem.portal_sae.repositories.ContactRepository;
import com.siscem.portal_sae.repositories.Relationships.IdentificationTypesRepository;

@Service
public class ContactService {

	@Autowired
	ContactRepository contactRepository;

	@Autowired
	IdentificationTypesRepository identificationTypeRepository;

	@Autowired
	AccountRepository accountRepository;

	/**
	 * Retrieves contacts associated with a given account code and maps them to
	 * DTOs.
	 * 
	 * @param accountCode The code of the account for which contacts are retrieved.
	 * @return A list of ContactDTO objects.
	 */
	public ResponseEntity<List<ContactDTO>> getContactsForAccount(Integer accountCode) {
		List<Contact> contacts = contactRepository.findByCuentaCodigo(accountCode);

		ModelMapper modelMapper = new ModelMapper();
		TypeMap<Contact, ContactDTO> typeMap = modelMapper.createTypeMap(Contact.class, ContactDTO.class);
		typeMap.addMappings(mp -> {
			mp.skip(ContactDTO::setTipoIdentificacion);
			mp.skip(ContactDTO::setCuenta);
		});

		return ResponseEntity.ok(contacts.stream().map(contact -> {
			ContactDTO contactoDTO = modelMapper.map(contact, ContactDTO.class);
			contactoDTO.setTipoIdentificacion(contact.getTipoIdentificacion().getNombreTipo());
			contactoDTO.setCuenta(contact.getCuenta().getPaginaWeb());
			return contactoDTO;
		}).collect(Collectors.toList()));
	}

	/**
	 * Creates a new contact based on the provided ContactCreateDTO.
	 * 
	 * @param createContactDTO The DTO containing information for creating the
	 *                         contact.
	 * @return A response entity indicating the success or failure of the operation.
	 */
	public ResponseEntity<String> createContact(ContactCreateDTO createContactDTO) {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addMappings(new PropertyMap<ContactCreateDTO, Contact>() {
			@Override
			protected void configure() {
				skip(destination.getCodigo());
			}
		});

		Contact contact = modelMapper.map(createContactDTO, Contact.class);
		contact.setTipoIdentificacion(
				identificationTypeRepository.findById(createContactDTO.getTipoIdentificacion()).orElse(null));
		contact.setCuenta(accountRepository.findById(createContactDTO.getCuenta()).orElse(null));

		if (contact.getTipoIdentificacion() == null || contact.getCuenta() == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contacto no encontrado");
		}

		try {
			contactRepository.save(contact);
			return ResponseEntity.ok("Contacto creado");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el contacto");
		}
	}

	/**
	 * Edits an existing contact based on the provided ContactEditDTO.
	 * 
	 * @param contactCode    The code of the contact to be edited.
	 * @param editContactDTO The DTO containing information for editing the contact.
	 * @return A response entity indicating the success or failure of the operation.
	 */
	public ResponseEntity<String> editContact(Integer contactCode, ContactEditDTO editContactDTO) {
		Optional<Contact> existingContactOptional = contactRepository.findById(contactCode);

		if (existingContactOptional.isPresent()) {
			Contact existingContact = existingContactOptional.get();

			ModelMapper modelMapper = new ModelMapper();
			modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
			modelMapper.addMappings(new PropertyMap<ContactEditDTO, Contact>() {
				@Override
				protected void configure() {
					skip(destination.getCodigo());
				}
			});

			modelMapper.map(editContactDTO, existingContact);

			editContactDTO.getTipoIdentificacion().ifPresent(id -> existingContact
					.setTipoIdentificacion(identificationTypeRepository.findById(id).orElse(null)));
			editContactDTO.getCuenta()
					.ifPresent(id -> existingContact.setCuenta(accountRepository.findById(id).orElse(null)));

			try {
				contactRepository.save(existingContact);
				return ResponseEntity.ok("Contacto actualizado");
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el contacto");
			}
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contacto no encontrado");
		}
	}

	/**
	 * Deletes a contact with the given contact code.
	 * 
	 * @param contactCode The code of the contact to be deleted.
	 * @return A response entity indicating the success or failure of the operation.
	 */
	public ResponseEntity<String> deleteContact(Integer contactCode) {
		try {
			if (contactRepository.existsById(contactCode)) {
				contactRepository.deleteById(contactCode);
				return ResponseEntity.ok("Contacto eliminado");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contacto no encontrado");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el contacto");
		}
	}
}
