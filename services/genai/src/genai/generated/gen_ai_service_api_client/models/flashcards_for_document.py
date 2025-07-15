from collections.abc import Mapping
from typing import TYPE_CHECKING, Any, TypeVar, Union

from attrs import define as _attrs_define
from attrs import field as _attrs_field

from ..types import UNSET, Unset

if TYPE_CHECKING:
    from ..models.flashcard import Flashcard


T = TypeVar("T", bound="FlashcardsForDocument")


@_attrs_define
class FlashcardsForDocument:
    """
    Attributes:
        document_id (Union[Unset, str]):
        flashcards (Union[Unset, list['Flashcard']]):
    """

    document_id: Union[Unset, str] = UNSET
    flashcards: Union[Unset, list["Flashcard"]] = UNSET
    additional_properties: dict[str, Any] = _attrs_field(init=False, factory=dict)

    def to_dict(self) -> dict[str, Any]:
        document_id = self.document_id

        flashcards: Union[Unset, list[dict[str, Any]]] = UNSET
        if not isinstance(self.flashcards, Unset):
            flashcards = []
            for flashcards_item_data in self.flashcards:
                flashcards_item = flashcards_item_data.to_dict()
                flashcards.append(flashcards_item)

        field_dict: dict[str, Any] = {}
        field_dict.update(self.additional_properties)
        field_dict.update({})
        if document_id is not UNSET:
            field_dict["document_id"] = document_id
        if flashcards is not UNSET:
            field_dict["flashcards"] = flashcards

        return field_dict

    @classmethod
    def from_dict(cls: type[T], src_dict: Mapping[str, Any]) -> T:
        from ..models.flashcard import Flashcard

        d = dict(src_dict)
        document_id = d.pop("document_id", UNSET)

        flashcards = []
        _flashcards = d.pop("flashcards", UNSET)
        for flashcards_item_data in _flashcards or []:
            flashcards_item = Flashcard.from_dict(flashcards_item_data)

            flashcards.append(flashcards_item)

        flashcards_for_document = cls(
            document_id=document_id,
            flashcards=flashcards,
        )

        flashcards_for_document.additional_properties = d
        return flashcards_for_document

    @property
    def additional_keys(self) -> list[str]:
        return list(self.additional_properties.keys())

    def __getitem__(self, key: str) -> Any:
        return self.additional_properties[key]

    def __setitem__(self, key: str, value: Any) -> None:
        self.additional_properties[key] = value

    def __delitem__(self, key: str) -> None:
        del self.additional_properties[key]

    def __contains__(self, key: str) -> bool:
        return key in self.additional_properties
