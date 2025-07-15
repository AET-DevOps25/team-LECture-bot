from collections.abc import Mapping
from typing import Any, TypeVar

from attrs import define as _attrs_define
from attrs import field as _attrs_field

T = TypeVar("T", bound="IndexRequest")


@_attrs_define
class IndexRequest:
    """
    Attributes:
        document_id (str):  Example: doc-123.
        course_space_id (str):  Example: cs-456.
        text_content (str):  Example: This is the content of the document..
    """

    document_id: str
    course_space_id: str
    text_content: str
    additional_properties: dict[str, Any] = _attrs_field(init=False, factory=dict)

    def to_dict(self) -> dict[str, Any]:
        document_id = self.document_id

        course_space_id = self.course_space_id

        text_content = self.text_content

        field_dict: dict[str, Any] = {}
        field_dict.update(self.additional_properties)
        field_dict.update(
            {
                "document_id": document_id,
                "course_space_id": course_space_id,
                "text_content": text_content,
            }
        )

        return field_dict

    @classmethod
    def from_dict(cls: type[T], src_dict: Mapping[str, Any]) -> T:
        d = dict(src_dict)
        document_id = d.pop("document_id")

        course_space_id = d.pop("course_space_id")

        text_content = d.pop("text_content")

        index_request = cls(
            document_id=document_id,
            course_space_id=course_space_id,
            text_content=text_content,
        )

        index_request.additional_properties = d
        return index_request

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
