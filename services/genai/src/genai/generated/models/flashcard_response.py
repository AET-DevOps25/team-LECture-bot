from collections.abc import Mapping
from typing import TYPE_CHECKING, Any, TypeVar, Union, cast

from attrs import define as _attrs_define
from attrs import field as _attrs_field

from ..types import UNSET, Unset

if TYPE_CHECKING:
    from ..models.flashcards_for_document import FlashcardsForDocument


T = TypeVar("T", bound="FlashcardResponse")


@_attrs_define
class FlashcardResponse:
    """
    Attributes:
        course_space_id (Union[Unset, str]):
        flashcards (Union[Unset, list['FlashcardsForDocument']]):
        error (Union[None, Unset, str]): An error message if the flashcard generation failed. Example: Failed to
            retrieve documents from the vector store..
    """

    course_space_id: Union[Unset, str] = UNSET
    flashcards: Union[Unset, list["FlashcardsForDocument"]] = UNSET
    error: Union[None, Unset, str] = UNSET
    additional_properties: dict[str, Any] = _attrs_field(init=False, factory=dict)

    def to_dict(self) -> dict[str, Any]:
        course_space_id = self.course_space_id

        flashcards: Union[Unset, list[dict[str, Any]]] = UNSET
        if not isinstance(self.flashcards, Unset):
            flashcards = []
            for flashcards_item_data in self.flashcards:
                flashcards_item = flashcards_item_data.to_dict()
                flashcards.append(flashcards_item)

        error: Union[None, Unset, str]
        if isinstance(self.error, Unset):
            error = UNSET
        else:
            error = self.error

        field_dict: dict[str, Any] = {}
        field_dict.update(self.additional_properties)
        field_dict.update({})
        if course_space_id is not UNSET:
            field_dict["course_space_id"] = course_space_id
        if flashcards is not UNSET:
            field_dict["flashcards"] = flashcards
        if error is not UNSET:
            field_dict["error"] = error

        return field_dict

    @classmethod
    def from_dict(cls: type[T], src_dict: Mapping[str, Any]) -> T:
        from ..models.flashcards_for_document import FlashcardsForDocument

        d = dict(src_dict)
        course_space_id = d.pop("course_space_id", UNSET)

        flashcards = []
        _flashcards = d.pop("flashcards", UNSET)
        for flashcards_item_data in _flashcards or []:
            flashcards_item = FlashcardsForDocument.from_dict(flashcards_item_data)

            flashcards.append(flashcards_item)

        def _parse_error(data: object) -> Union[None, Unset, str]:
            if data is None:
                return data
            if isinstance(data, Unset):
                return data
            return cast(Union[None, Unset, str], data)

        error = _parse_error(d.pop("error", UNSET))

        flashcard_response = cls(
            course_space_id=course_space_id,
            flashcards=flashcards,
            error=error,
        )

        flashcard_response.additional_properties = d
        return flashcard_response

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
